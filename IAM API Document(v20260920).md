# 接口文档 

## 1. 通用约定

### 1.1 请求方式

- 通信协议：`HTTP POST`
- 报文格式：`application/json`（UTF-8）
- 除特殊说明外，参数均通过 JSON 请求体传递；简单参数接口（如 `sendRegSms`）可用 JSON 键值对传递。

### 1.2 常规返回结构 `RstResult<T>`

```json
{
  "code": "结果码（业务成功时返回 00000000，其它情况则表示业务失败。）",
  "message": "提示信息",
  "data": "业务数据 T"
}
```

### 1.3 分页返回结构 `PageResult<T>`

```json
{
  "code": "结果码",
  "message": "提示信息",
  "pageIndex": 1,		//当前页码
  "pageSize": 20,		//每页记录数
  "totalRecord": 100,		//总记录数
  "totalPages": 5,		//总页数
  "data": [ "业务数据列表" ]
}
```

## 2、组织机构管理  
#### 2.1 公司列表
**接口路径**：`POST /iam/listCompany`  
**请求参数**：  

```json
{
  "appId": "P1788682",	//应用标识（String，必填）
  "pageIndex":1,	//页码(int，选填）
  "pageSize":12		//每页条数（int，选填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": [
        {
            "name": "贝克尔有限公司",
            "id": "ff8080819f360237019f360602960000"
        }
    ],
    "pageIndex": 1,
    "totalPages": 1,
    "pageSize": 12,
    "message": "请求成功!",
    "totalRecord": 3
}
```

#### 2.2 公司新增
**接口路径**：`POST /iam/createCompany`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "name":"贝克尔有限公司"	//公司名称（String，必填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": "ff8080819f360237019f360602960000",
    "message": "请求成功!"
}
```

#### 2.3 部门列表
**接口路径**：`POST /iam/listDepartment`  
**请求参数**：  

```json
{
  "companyId":"ff80860000",	//所属公司标识（String，选填，不传则查询全部部门）
  "pageIndex":1,	//页码（int，选填）
  "pageSize":12		//每页条数（int，选填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": [
        {
            "id": "ff8080360602960001",
            "code": "DEV",
            "name": "研发部"
        }
    ],
    "pageIndex": 1,
    "totalPages": 1,
    "pageSize": 12,
    "message": "请求成功!",
    "totalRecord": 1
}
```

#### 2.4 部门新增
**接口路径**：`POST /iam/createDepartment`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "name":"研发部",	//部门名称（String，必填）
  "companyId":"ff80802960000"	//所属公司标识（String，必填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": "ff8080819f60602960001",
    "message": "请求成功!"
}
```

#### 2.5 部门修改

#### 2.6 员工列表
**接口路径**：`POST /iam/listPerson`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填，服务端强制按 appId 过滤，防止跨应用查询）
  "name":"张",	//姓名关键字（String，选填，模糊匹配）
  "pageIndex":1,	//页码（int，选填）
  "pageSize":12		//每页条数（int，选填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": [
        {
            "id": "ff8080819f360237019f360602960002",
            "code": "P0001",
            "realname": "张三",
            "idCard": "3301**********0011",
            "statusId": "ENABLE"
        }
    ],
    "pageIndex": 1,
    "totalPages": 1,
    "pageSize": 12,
    "message": "请求成功!",
    "totalRecord": 1
}
```

#### 2.7 员工增加
**接口路径**：`POST /iam/createPerson`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "name":"张三",	//用户姓名（String，必填，为空返回 40000001）
  "phone":"13800138000",	//手机号（String，必填，与姓名均不能为空）
  "idcard":"3301**********0011",	//身份证号（String，选填，重复时返回身份证号重复错误）
  "code":"P0001",	//人员编号（String，选填）
  "statusId":"ENABLE",	//状态标识（String，选填）
  "email":"zhangsan@example.com",	//邮箱（String，选填）
  "tags":["VIP"],	//标签列表（List，选填）
  "loginId":10001		//关联的登录账号标识（String/Long，选填，传入后绑定登录账号）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": "ff8080819f360237019f360602960002",
    "message": "请求成功!"
}
```

#### 2.8 员工查询
**接口路径**：`GET /iam/getPerson/{partyId}`  
**请求参数**：无请求体，人员标识通过路径参数传递。  

```
/iam/getPerson/ff8080819f360237019f360602960002
```

- `partyId`（String，必填）：人员标识

**响应报文**：

```json
{
    "code": "00000000",
    "data": {
        "id": "ff8080819f360237019f360602960002",
        "code": "P0001",
        "realname": "张三",
        "idCard": "3301**********0011",
        "statusId": "ENABLE"
    },
    "message": "请求成功!"
}
```

#### 2.9 员工修改
**接口路径**：`PUT /iam/updatePerson`  
**请求参数**：  

```json
{
  "personId":"ff8080819f360237019f360602960002",	//人员标识（String，必填，为空返回 40000003）
  "code":"P0001",	//人员编号（String，选填）
  "name":"张三丰",	//姓名（String，选填）
  "sex":"M",		//性别（String/Char，选填）
  "birthday":"1990-01-01",	//出生日期（Date/String，选填）
  "iconId":"icon-001",	//头像标识（String，选填）
  "idCard":"3301**********0011"	//身份证号（String，选填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": null,
    "message": "请求成功!"
}
```

#### 2.10 员工状态变更

## 3、角色资源管理  

#### 3.1 账号列表
**接口路径**：`POST /iam/listLogin`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "loginName":"zhangsan",	//登录账号（String，选填）
  "userName":"张三",	//用户姓名（String，选填）
  "statusId":"ENABLE",	//状态标识（String，选填）
  "pageIndex":1,	//页码（int，选填）
  "pageSize":12		//每页条数（int，选填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": [
        {
            "loginId": 10001,
            "loginName": "zhangsan",
            "nickName": "张三",
            "loginStatus": "ENABLE"
        }
    ],
    "pageIndex": 1,
    "totalPages": 1,
    "pageSize": 12,
    "message": "请求成功!",
    "totalRecord": 1
}
```

#### 3.2 角色列表
**接口路径**：`POST /iam/listRole`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "roleCode":"ADMIN",	//角色编码（String，选填）
  "roleName":"管理员",	//角色名称（String，选填）
  "pageIndex":1,	//页码（int，选填）
  "pageSize":12		//每页条数（int，选填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": [
        {
            "roleId": 1,
            "appId": "BHT_FFZ",
            "roleCode": "ADMIN",
            "roleName": "管理员",
            "statusId": "ENABLE",
            "sequence": "1",
            "roleRemark": "系统管理员",
            "createTime": "2026-09-21 10:00:00"
        }
    ],
    "pageIndex": 1,
    "totalPages": 1,
    "pageSize": 12,
    "message": "请求成功!",
    "totalRecord": 1
}
```

#### 3.3 角色创建
**接口路径**：`POST /iam/addRole`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "roleCode":"ADMIN",	//角色编码（String，必填）
  "roleName":"管理员",	//角色名称（String，必填）
  "statusId":"ENABLE",	//状态标识（String，选填）
  "sequence":"1",	//排序号（String，选填）
  "roleRemark":"系统管理员"	//角色备注（String，选填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": "1",
    "message": "请求成功!"
}
```

#### 3.4 角色修改
**接口路径**：`POST /iam/updateRole`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "roleId":1,	//角色标识（Long，必填）
  "roleCode":"ADMIN",	//角色编码（String，必填）
  "roleName":"超级管理员",	//角色名称（String，必填）
  "statusId":"ENABLE",	//状态标识（String，选填）
  "sequence":"1",	//排序号（String，选填）
  "roleRemark":"系统超级管理员"	//角色备注（String，选填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": "1",
    "message": "请求成功!"
}
```

#### 3.5 角色删除
**接口路径**：`POST /iam/deleteRole`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "roleId":1		//角色标识（Long，必填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": null,
    "message": "请求成功!"
}
```

#### 3.6 账号分配角色
**接口路径**：`POST /iam/addLoginRole`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "loginId":10001,	//登录账号标识（Long，必填）
  "roleId":1		//角色标识（Long，必填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": true,
    "message": "请求成功!"
}
```

#### 3.7 账号移除角色
**接口路径**：`POST /iam/removeLoginRole`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "loginId":10001,	//登录账号标识（Long，必填）
  "roleId":1		//角色标识（Long，必填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": null,
    "message": "请求成功!"
}
```

#### 3.8 资源列表
**接口路径**：`POST /iam/listResource`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "resType":"Menu",	//资源类型（String，选填）：System-子系统/Menu-菜单/Function-功能
  "resCode":"user:list",	//资源编号（String，选填）
  "resName":"用户管理",	//资源名称（String，选填）
  "pageIndex":1,	//页码（int，选填）
  "pageSize":12		//每页条数（int，选填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": [
        {
            "resId": "1001",
            "parentId": "0",
            "resType": "Menu",
            "resCode": "user:list",
            "resName": "用户管理",
            "resIcon": "icon-user",
            "resUrl": "/system/user",
            "resRemark": "用户管理菜单",
            "sequence": 1,
            "statusId": "ENABLE"
        }
    ],
    "pageIndex": 1,
    "totalPages": 1,
    "pageSize": 12,
    "message": "请求成功!",
    "totalRecord": 1
}
```

#### 3.9 资源创建
**接口路径**：`POST /iam/createResource`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "parentId":"0",	//父资源标识（String，选填，构成树结构）
  "resType":"Menu",	//资源类型（String，必填）：System/Menu/Function
  "resCode":"user:list",	//资源编号/权限标识（String，必填）
  "resName":"用户管理",	//资源名称（String，必填）
  "resIcon":"icon-user",	//资源图标（String，选填）
  "resUrl":"/system/user",	//资源跳转地址（String，选填）
  "resRemark":"用户管理菜单",	//资源描述（String，选填）
  "sequence":1,		//排序号（Integer，选填）
  "statusId":"ENABLE"	//状态标识（String，选填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": "1001",
    "message": "请求成功!"
}
```

#### 3.10 资源修改
**接口路径**：`POST /iam/updateResource`  
**请求参数**：  

```json
{
  "resId":"1001",	//资源标识（String，必填）
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "parentId":"0",	//父资源标识（String，选填）
  "resType":"Menu",	//资源类型（String，必填）：System/Menu/Function
  "resCode":"user:list",	//资源编号/权限标识（String，必填）
  "resName":"用户管理",	//资源名称（String，必填）
  "resIcon":"icon-user",	//资源图标（String，选填）
  "resUrl":"/system/user",	//资源跳转地址（String，选填）
  "resRemark":"用户管理菜单",	//资源描述（String，选填）
  "sequence":1		//排序号（Integer，选填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": "1001",
    "message": "请求成功!"
}
```

#### 3.11 资源删除
**接口路径**：`POST /iam/deleteResource`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "resId":"1001"		//资源标识（String，必填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": null,
    "message": "请求成功!"
}
```

#### 3.12 角色分配资源
**接口路径**：`POST /iam/grantRolePrivilege`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "roleId":1,		//角色标识（Long，必填）
  "resourceIdList":[1001,1002]	//资源标识列表（List<Long>，必填，增量授予，不影响其他资源）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": true,
    "message": "请求成功!"
}
```

#### 3.13 角色移除资源
**接口路径**：`POST /iam/revokeRolePrivilege`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "roleId":1,		//角色标识（Long，必填）
  "resourceIdList":[1001,1002]	//资源标识列表（List<Long>，必填，增量撤销，不影响其他资源）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": true,
    "message": "请求成功!"
}
```

## 4、账号认证管理  
#### 4.1 注册账号（用户名＋密码模式）
**接口路径**：`POST /iam/pwdRegister`  
**请求参数**：  

```json
{
  "appId":"49ba59abb",	//应用标识（String，必填）
  "username":"zhangsan",	//注册账号（String，必填）
  "password":"e10af20f883e",	//注册密码（String，必填，MD5 加密后传输）
  "password2":"e10a20f883e",	//二次密码（String，选填，不为空时会校验二者的一致性）
  "yqCode":"ABC123",	//邀请码（String，选填，系统开启邀请码验证时必填）
  "yzCode":"8888",	//图形验证码（String，选填，系统开启验证码验证时必填）
  "nickName":"张三",	//用户昵称（String，选填）
  "roleList":["ADMIN"]	//注册成功后直接授予的角色列表（List<String>，选填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": "注册成功",
    "message": "请求成功!"
}
```

#### 4.2 注册账号（手机号＋短信码模式）
**接口路径**：`POST /iam/smsRegister`  
**请求参数**：  

```json
{
  "phone":"13800138000",	//注册手机号（String，必填）
  "validCode":"123456",	//短信验证码（String，必填，先调用发送注册短信验证码获取）
  "inviteCode":"ABC123"	//邀请码（String，选填）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": "注册成功",
    "message": "请求成功!"
}
```

#### 2.3 登录账号
**接口路径**：`POST /iam/pwdLogin`  
**请求参数**：  

```json
{
  "username":"13800138000",	//登录账号/手机号（String，必填）
  "password":"e10adc3949ba59abbe56e057f20f883e"	//登录密码（String，必填，MD5 加密后传输）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": {
        "accessToken": "ff8080819f360237019f360602960000",
        "loginName": "13800138000",
        "nickName": "张三",
        "userIcon": "",
        "status": "ENABLE",
        "roleList": ["ADMIN"],
        "createTime": "2026-09-21 10:00:00"
    },
    "message": "请求成功!"
}
```

#### 2.4 登出账号
**接口路径**：`POST /iam/logout`  
**请求参数**：无请求体，登录授权码通过请求头 `Authorization` 传递。  

```
Authorization: ff8080819f360237019f360602960000
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": "退出成功",
    "message": "请求成功!"
}
```

#### 2.5 修改账号密码
**接口路径**：`POST /iam/changePassword`  
**请求参数**：  

```json
{
  "loginId":10001,	//登录标识（long，必填）
  "oldPasswd":"e10adc3949ba59abbe56e057f20f883e",	//旧密码（String，必填，MD5 加密后传输）
  "newPasswd":"d8578edf8458ce06fbc5bb76a58c5ca4",	//新密码（String，必填，MD5 加密后传输）
  "newPasswdAgain":"d8578edf8458ce06fbc5bb76a58c5ca4"	//新密码二次输入（String，必填，MD5 加密后传输）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": "修改成功",
    "message": "请求成功!"
}
```

#### 2.6 重置账号密码
**接口路径**：`POST /iam/resetPassword`  
**请求参数**：  

```json
{
  "appId":"BHT_FFZ",	//应用标识（String，必填）
  "loginName":"zhangsan",	//登录账户（String，必填）
  "newPassword":"d8578edf8458ce06fbc5bb76a58c5ca4"	//新密码（String，选填，MD5 加密后传输；不传则使用应用默认密码）
}
```
**响应报文**：

```json
{
    "code": "00000000",
    "data": "重置成功",
    "message": "请求成功!"
}
```

#### 2.7 更新账号资料
--资料包含：