--初始化角色
insert into `sys_role` (`app_id`, `role_code`, `role_name`, `role_remark`, `status_id`) values ('IAM_CENTER', 'super_admin', '超级管理员', '超级管理员角色', 'ENABLE');
--初始化账号（密码：123456）
insert into `sys_login` (`app_id`, `login_name`, `login_pwd`, `login_state`, `nick_name`, `version`) values ('IAM_CENTER', 'adminx', '$2a$10$NG1ia4UJRvgFyrF8ar7lKObUkiihSElzyUlLfxBkzPSTotMSzuYJS', 'ENABLE', '超级管理员', 'v1.0.0');
--初始化账号的角色
insert into `sys_login_role` (login_id, role_id) select login.login_id, role.id from sys_login as login, sys_role as role where login.login_name = 'adminx' and role.role_code = 'super_admin';