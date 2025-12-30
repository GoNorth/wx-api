# BizStore saveWithFiles 接口请求示例

## 接口信息
- **URL**: `POST /manage/bizStore/saveWithFiles`
- **Content-Type**: `multipart/form-data`

## 请求参数说明

### 门店基本信息（BizStore 字段）

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| storeId | String | 否 | 门店ID，不传则自动生成 | "STORE_001" |
| ownerOpenid | String | 是 | 店主微信OPENID，关联WX_USER表的OPENID | "wx_openid_001" |
| ownerName | String | 是 | 店主姓名 | "张三" |
| ownerPhone | String | 是 | 店主手机号（11位） | "13800138000" |
| storeName | String | 是 | 门店名称 | "测试餐厅" |
| cateringType | String | 是 | 餐饮种类：CHINESE-中餐，WESTERN-西餐，JAPANESE-日料，HOTPOT-火锅，BARBECUE-烧烤，FASTFOOD-快餐，SNACK-小吃，OTHER-其他 | "CHINESE" |
| storeAddress | String | 是 | 门店详细地址 | "北京市朝阳区测试街道123号" |
| longitude | BigDecimal | 否 | 经度 | 116.1234567 |
| latitude | BigDecimal | 否 | 纬度 | 39.1234567 |
| customerGroup | String | 是 | 客户人群：STUDENT-学生，OFFICE_WORKER-上班族，FAMILY-家庭，BUSINESS-商务人士，ELDERLY-老年人，OTHER-其他 | "OFFICE_WORKER" |
| auditStatus | Integer | 否 | 审核状态：0-待审核，1-已通过，2-已拒绝（默认0） | 0 |
| auditRemark | String | 否 | 审核备注 | "审核通过" |
| salesId | String | 否 | 所属销售员ID，关联BIZ_SALES表的SALES_ID | "SALES_001" |

### 门店VI文件

| 参数名 | 类型 | 必填 | 说明 | 文件格式 |
|--------|------|------|------|----------|
| logoFile | MultipartFile | 否 | 门店LOGO文件 | JPG/PNG |
| workUniformFile | MultipartFile | 否 | 工作服照片文件 | JPG/PNG |
| ipImageFile | MultipartFile | 否 | IP形象设计图文件 | JPG/PNG |

### 人物形象文件

| 参数名 | 类型 | 必填 | 说明 | 文件格式 |
|--------|------|------|------|----------|
| characterPhotoFile | MultipartFile | 否 | 人物照片文件 | JPG/PNG |
| characterVoiceFile | MultipartFile | 否 | 人物声音文件 | MP3/WAV |
| characterRole | String | 否 | 人物角色：OWNER-店主，CHEF-厨师，WAITER-服务员，MANAGER-店长，OTHER-其他 | "OWNER" |

## 请求示例

### cURL 示例

```bash
curl -X POST "http://localhost:8080/manage/bizStore/saveWithFiles" \
  -H "Content-Type: multipart/form-data" \
  -F "ownerOpenid=wx_openid_001" \
  -F "ownerName=张三" \
  -F "ownerPhone=13800138000" \
  -F "storeName=测试餐厅" \
  -F "cateringType=CHINESE" \
  -F "storeAddress=北京市朝阳区测试街道123号" \
  -F "longitude=116.1234567" \
  -F "latitude=39.1234567" \
  -F "customerGroup=OFFICE_WORKER" \
  -F "auditStatus=0" \
  -F "logoFile=@/path/to/logo.jpg" \
  -F "workUniformFile=@/path/to/uniform.jpg" \
  -F "ipImageFile=@/path/to/ip_image.jpg" \
  -F "characterPhotoFile=@/path/to/character_photo.jpg" \
  -F "characterVoiceFile=@/path/to/character_voice.mp3" \
  -F "characterRole=OWNER"
```

### JavaScript (FormData) 示例

```javascript
const formData = new FormData();

// 门店基本信息
formData.append('ownerOpenid', 'wx_openid_001');
formData.append('ownerName', '张三');
formData.append('ownerPhone', '13800138000');
formData.append('storeName', '测试餐厅');
formData.append('cateringType', 'CHINESE');
formData.append('storeAddress', '北京市朝阳区测试街道123号');
formData.append('longitude', '116.1234567');
formData.append('latitude', '39.1234567');
formData.append('customerGroup', 'OFFICE_WORKER');
formData.append('auditStatus', '0');

// 门店VI文件
formData.append('logoFile', logoFileInput.files[0]);
formData.append('workUniformFile', workUniformFileInput.files[0]);
formData.append('ipImageFile', ipImageFileInput.files[0]);

// 人物形象文件
formData.append('characterPhotoFile', characterPhotoFileInput.files[0]);
formData.append('characterVoiceFile', characterVoiceFileInput.files[0]);
formData.append('characterRole', 'OWNER');

fetch('/manage/bizStore/saveWithFiles', {
  method: 'POST',
  body: formData
})
.then(response => response.json())
.then(data => console.log(data));
```

### Postman 请求示例

1. **Method**: POST
2. **URL**: `http://localhost:8080/manage/bizStore/saveWithFiles`
3. **Body**: 选择 `form-data`
4. **参数设置**:

   **文本字段**:
   - `ownerOpenid`: `wx_openid_001` (Text)
   - `ownerName`: `张三` (Text)
   - `ownerPhone`: `13800138000` (Text)
   - `storeName`: `测试餐厅` (Text)
   - `cateringType`: `CHINESE` (Text)
   - `storeAddress`: `北京市朝阳区测试街道123号` (Text)
   - `longitude`: `116.1234567` (Text)
   - `latitude`: `39.1234567` (Text)
   - `customerGroup`: `OFFICE_WORKER` (Text)
   - `auditStatus`: `0` (Text)
   - `characterRole`: `OWNER` (Text)

   **文件字段**:
   - `logoFile`: 选择文件 (File)
   - `workUniformFile`: 选择文件 (File)
   - `ipImageFile`: 选择文件 (File)
   - `characterPhotoFile`: 选择文件 (File)
   - `characterVoiceFile`: 选择文件 (File)

### Java (RestTemplate) 示例

```java
RestTemplate restTemplate = new RestTemplate();
HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.MULTIPART_FORM_DATA);

MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

// 门店基本信息
body.add("ownerOpenid", "wx_openid_001");
body.add("ownerName", "张三");
body.add("ownerPhone", "13800138000");
body.add("storeName", "测试餐厅");
body.add("cateringType", "CHINESE");
body.add("storeAddress", "北京市朝阳区测试街道123号");
body.add("longitude", "116.1234567");
body.add("latitude", "39.1234567");
body.add("customerGroup", "OFFICE_WORKER");
body.add("auditStatus", "0");
body.add("characterRole", "OWNER");

// 文件
body.add("logoFile", new FileSystemResource(new File("/path/to/logo.jpg")));
body.add("workUniformFile", new FileSystemResource(new File("/path/to/uniform.jpg")));
body.add("ipImageFile", new FileSystemResource(new File("/path/to/ip_image.jpg")));
body.add("characterPhotoFile", new FileSystemResource(new File("/path/to/character_photo.jpg")));
body.add("characterVoiceFile", new FileSystemResource(new File("/path/to/character_voice.mp3")));

HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
ResponseEntity<R> response = restTemplate.postForEntity(
    "http://localhost:8080/manage/bizStore/saveWithFiles", 
    requestEntity, 
    R.class
);
```

## 响应示例

### 成功响应

```json
{
  "code": 0,
  "msg": "success"
}
```

### 失败响应

```json
{
  "code": 500,
  "msg": "门店信息不得为空"
}
```

## 注意事项

1. 所有文件参数都是可选的（`required = false`），只有上传的文件才会被处理和保存
2. `storeId` 如果不传，系统会自动生成 UUID
3. 文件上传后会保存到 OSS，返回的文件 URL 会自动保存到对应的数据库字段中
4. 门店VI和人物形象数据会自动关联到门店ID
5. 所有操作都在事务中执行，确保数据一致性

