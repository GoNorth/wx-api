# -*- coding: utf-8 -*-
import yaml
import json
import os
import glob

workflow_dir = r"D:\code4\51_computer\maven\yrzl_gui_auto22\dify-工作流\PC-52右侧"
output_dir = os.path.dirname(__file__)

all_vars = []
files_analyzed = []

for yml_file in glob.glob(os.path.join(workflow_dir, "*.yml")):
    try:
        with open(yml_file, 'r', encoding='utf-8') as f:
            data = yaml.safe_load(f)
        
        if 'workflow' in data and 'graph' in data['workflow']:
            nodes = data['workflow']['graph'].get('nodes', [])
            for node in nodes:
                if node.get('data', {}).get('type') == 'start':
                    vars_list = node.get('data', {}).get('variables', [])
                    for v in vars_list:
                        if v.get('variable'):
                            v['source_file'] = os.path.basename(yml_file)
                            all_vars.append(v)
                    if vars_list:
                        files_analyzed.append(os.path.basename(yml_file))
                    break
    except Exception as e:
        print(f"Error processing {os.path.basename(yml_file)}: {e}")

# 去重
seen = {}
unique_vars = []
for v in all_vars:
    var_name = v['variable']
    if var_name not in seen:
        seen[var_name] = True
        unique_vars.append(v)

# 生成 Markdown
md = "# Dify 工作流输入参数分析结果（完整汇总）\n\n"
md += "## 分析说明\n"
md += "- 分析对象：Dify 工作流 YAML 文件\n"
md += "- 提取位置：`workflow.graph.nodes` 中 `type: start` 节点的 `variables` 字段\n"
md += "- 去重规则：按 `variable`（变量名）去重，保留第一个出现的配置\n\n"

md += f"## 已分析文件（共 {len(files_analyzed)} 个）\n\n"
for i, f in enumerate(sorted(files_analyzed), 1):
    md += f"{i}. {f}\n"
md += "\n---\n\n"

md += "## 输入参数列表（去重后）\n\n"
md += "| 变量名 | 标签 | 类型 | 必填 | 最大长度 | 选项数量 | 来源文件 |\n"
md += "|--------|------|------|------|----------|----------|----------|\n"

for v in sorted(unique_vars, key=lambda x: x['variable']):
    opt_count = len(v.get('options', []))
    opt_str = str(opt_count) if opt_count > 0 else '-'
    max_len = v.get('max_length', '-')
    req = '是' if v.get('required') else '否'
    md += f"| {v['variable']} | {v['label']} | {v['type']} | {req} | {max_len} | {opt_str} | {v['source_file']} |\n"

md += "\n---\n\n## 详细参数信息\n\n"

for v in sorted(unique_vars, key=lambda x: x['variable']):
    md += f"### {v['variable']} ({v['label']})\n\n"
    md += f"- **类型**: {v['type']}\n"
    md += f"- **必填**: {'是' if v.get('required') else '否'}\n"
    if v.get('max_length'):
        md += f"- **最大长度**: {v['max_length']}\n"
    if v.get('options'):
        opts = v['options'][:10]
        md += f"- **选项**: {', '.join(opts)}"
        if len(v['options']) > 10:
            md += f" ... (共 {len(v['options'])} 个选项)"
        md += "\n"
    if v.get('allowed_file_types'):
        md += f"- **允许的文件类型**: {', '.join(v['allowed_file_types'])}\n"
    if v.get('allowed_file_upload_methods'):
        md += f"- **允许的上传方式**: {', '.join(v['allowed_file_upload_methods'])}\n"
    md += f"- **来源文件**: {v['source_file']}\n\n"

# 统计
md += "---\n\n## 统计信息\n\n"
total = len(unique_vars)
required = sum(1 for v in unique_vars if v.get('required'))
optional = total - required

type_dist = {}
for v in unique_vars:
    t = v['type']
    type_dist[t] = type_dist.get(t, 0) + 1

md += f"- **总参数数**: {total}\n"
md += f"- **必填参数**: {required}\n"
md += f"- **可选参数**: {optional}\n"
md += f"- **类型分布**:\n"
for t, c in sorted(type_dist.items()):
    md += f"  - {t}: {c}个\n"

# 保存
output_file = os.path.join(output_dir, "分析结果_工作流输入参数.md")
with open(output_file, 'w', encoding='utf-8') as f:
    f.write(md)

print(f"完成！共分析 {len(files_analyzed)} 个文件，提取 {len(all_vars)} 个参数（去重前），去重后 {len(unique_vars)} 个唯一参数")
print(f"结果已保存到: {output_file}")

