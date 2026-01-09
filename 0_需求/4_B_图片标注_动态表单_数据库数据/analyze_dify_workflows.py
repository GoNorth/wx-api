#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Dify 工作流输入参数分析脚本
分析所有 YAML 文件中的输入参数，合并去重
"""

import os
import yaml
import json
from pathlib import Path
from typing import Dict, List, Set

def extract_variables_from_yaml(file_path: str) -> List[Dict]:
    """从 YAML 文件中提取输入参数"""
    variables = []
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            data = yaml.safe_load(f)
        
        # 查找 start 节点
        if 'workflow' in data and 'graph' in data['workflow']:
            nodes = data['workflow']['graph'].get('nodes', [])
            for node in nodes:
                if node.get('data', {}).get('type') == 'start':
                    node_vars = node.get('data', {}).get('variables', [])
                    for var in node_vars:
                        var_info = {
                            'variable': var.get('variable', ''),
                            'label': var.get('label', ''),
                            'type': var.get('type', ''),
                            'required': var.get('required', False),
                            'max_length': var.get('max_length'),
                            'options': var.get('options', []),
                            'allowed_file_types': var.get('allowed_file_types', []),
                            'allowed_file_upload_methods': var.get('allowed_file_upload_methods', []),
                            'source_file': os.path.basename(file_path)
                        }
                        if var_info['variable']:  # 只添加有变量名的
                            variables.append(var_info)
                    break  # 只处理第一个 start 节点
    except Exception as e:
        print(f"处理文件 {file_path} 时出错: {e}")
    
    return variables

def merge_and_deduplicate(all_variables: List[Dict]) -> List[Dict]:
    """合并并去重变量，保留第一个出现的配置"""
    seen = {}
    result = []
    
    for var in all_variables:
        var_name = var['variable']
        if var_name not in seen:
            seen[var_name] = True
            result.append(var)
    
    return result

def generate_markdown_report(variables: List[Dict], analyzed_files: List[str]) -> str:
    """生成 Markdown 格式的报告"""
    md = "# Dify 工作流输入参数分析结果（完整汇总）\n\n"
    md += "## 分析说明\n"
    md += "- 分析对象：Dify 工作流 YAML 文件\n"
    md += "- 提取位置：`workflow.graph.nodes` 中 `type: start` 节点的 `variables` 字段\n"
    md += "- 去重规则：按 `variable`（变量名）去重，保留第一个出现的配置\n\n"
    
    md += f"## 已分析文件（共 {len(analyzed_files)} 个）\n\n"
    for i, filename in enumerate(analyzed_files, 1):
        md += f"{i}. {filename}\n"
    md += "\n---\n\n"
    
    md += "## 输入参数列表（去重后）\n\n"
    md += "| 变量名 | 标签 | 类型 | 必填 | 最大长度 | 选项数量 | 来源文件 |\n"
    md += "|--------|------|------|------|----------|----------|----------|\n"
    
    for var in variables:
        options_count = len(var.get('options', []))
        options_str = str(options_count) if options_count > 0 else '-'
        max_len = var.get('max_length', '-')
        required = '是' if var.get('required') else '否'
        md += f"| {var['variable']} | {var['label']} | {var['type']} | {required} | {max_len} | {options_str} | {var['source_file']} |\n"
    
    md += "\n---\n\n"
    md += "## 详细参数信息\n\n"
    
    for var in variables:
        md += f"### {var['variable']} ({var['label']})\n\n"
        md += f"- **类型**: {var['type']}\n"
        md += f"- **必填**: {'是' if var.get('required') else '否'}\n"
        if var.get('max_length'):
            md += f"- **最大长度**: {var['max_length']}\n"
        if var.get('options'):
            md += f"- **选项**: {', '.join(var['options'][:10])}"
            if len(var['options']) > 10:
                md += f" ... (共 {len(var['options'])} 个选项)"
            md += "\n"
        if var.get('allowed_file_types'):
            md += f"- **允许的文件类型**: {', '.join(var['allowed_file_types'])}\n"
        if var.get('allowed_file_upload_methods'):
            md += f"- **允许的上传方式**: {', '.join(var['allowed_file_upload_methods'])}\n"
        md += f"- **来源文件**: {var['source_file']}\n\n"
    
    # 统计信息
    md += "---\n\n"
    md += "## 统计信息\n\n"
    
    total = len(variables)
    required_count = sum(1 for v in variables if v.get('required'))
    optional_count = total - required_count
    
    type_dist = {}
    for var in variables:
        var_type = var['type']
        type_dist[var_type] = type_dist.get(var_type, 0) + 1
    
    md += f"- **总参数数**: {total}\n"
    md += f"- **必填参数**: {required_count}\n"
    md += f"- **可选参数**: {optional_count}\n"
    md += f"- **类型分布**:\n"
    for var_type, count in sorted(type_dist.items()):
        md += f"  - {var_type}: {count}个\n"
    
    return md

def main():
    # 工作流目录 - 使用绝对路径
    import sys
    import os
    workflow_dir = r"D:\code4\51_computer\maven\yrzl_gui_auto22\dify-工作流\PC-52右侧"
    # 输出目录 - 使用脚本所在目录
    script_dir = os.path.dirname(os.path.abspath(__file__))
    output_dir = script_dir
    
    # 获取所有 YAML 文件
    yaml_files = []
    for file in os.listdir(workflow_dir):
        if file.endswith('.yml') or file.endswith('.yaml'):
            yaml_files.append(os.path.join(workflow_dir, file))
    
    print(f"找到 {len(yaml_files)} 个 YAML 文件")
    
    # 提取所有变量
    all_variables = []
    analyzed_files = []
    
    for yaml_file in sorted(yaml_files):
        print(f"正在分析: {os.path.basename(yaml_file)}")
        vars_list = extract_variables_from_yaml(yaml_file)
        if vars_list:
            all_variables.extend(vars_list)
            analyzed_files.append(os.path.basename(yaml_file))
    
    print(f"\n共提取 {len(all_variables)} 个参数（去重前）")
    
    # 去重
    unique_variables = merge_and_deduplicate(all_variables)
    print(f"去重后剩余 {len(unique_variables)} 个唯一参数")
    
    # 生成报告
    markdown_report = generate_markdown_report(unique_variables, analyzed_files)
    
    # 保存 Markdown 文件 - 使用用户指定的文件名
    md_output_path = os.path.join(output_dir, "分析结果_工作流输入参数.md")
    with open(md_output_path, 'w', encoding='utf-8') as f:
        f.write(markdown_report)
    print(f"\nMarkdown 报告已保存到: {md_output_path}")
    
    # 生成 JSON 文件
    json_data = {
        "分析说明": {
            "分析对象": "Dify 工作流 YAML 文件",
            "提取位置": "workflow.graph.nodes 中 type: start 节点的 variables 字段",
            "去重规则": "按 variable（变量名）去重，保留第一个出现的配置"
        },
        "已分析文件": analyzed_files,
        "输入参数": unique_variables,
        "统计信息": {
            "总参数数": len(unique_variables),
            "必填参数": sum(1 for v in unique_variables if v.get('required')),
            "可选参数": sum(1 for v in unique_variables if not v.get('required')),
            "类型分布": {}
        }
    }
    
    # 计算类型分布
    for var in unique_variables:
        var_type = var['type']
        json_data["统计信息"]["类型分布"][var_type] = \
            json_data["统计信息"]["类型分布"].get(var_type, 0) + 1
    
    json_output_path = os.path.join(output_dir, "分析结果_工作流输入参数_完整汇总.json")
    with open(json_output_path, 'w', encoding='utf-8') as f:
        json.dump(json_data, f, ensure_ascii=False, indent=2)
    print(f"JSON 报告已保存到: {json_output_path}")
    
    print("\n分析完成！")

if __name__ == "__main__":
    main()

