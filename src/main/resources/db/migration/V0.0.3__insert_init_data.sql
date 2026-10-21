INSERT INTO member (id, ko_name, en_name, email, phone_number, password, is_active, created_at, updated_at)
VALUES ('userId', 'ko', 'en', 'userId@naver.com', '010-3453-6148',
        '$2a$12$KIq96pehUt70KRVFbi9hkuhioXK1uvrC8K.t.PmUfhdRYQb/P1FJi', true, now(), now());

INSERT INTO role_member (role_id, member_id)
VALUES ('ROLE_ADMIN', 'userId');

INSERT INTO code_group (id, name, description, sort_order, is_active, create_member_id, update_member_id)
VALUES ('TEST_GROUP_001', 'System Codes', 'Group for system-level codes', 1, true, 'admin', 'admin'),
       ('TEST_GROUP_002', 'User Codes', 'Group for user-related codes', 2, true, 'admin', 'admin'),
       ('TEST_GROUP_003', 'Notification Codes', 'Group for notification-related codes', 3, true, 'admin', 'admin');

INSERT INTO code (id, code_group_id, message_id, name, description, sort_order, is_active, create_member_id,
                  update_member_id)
VALUES ('TEST_CODE_001', 'TEST_GROUP_001', 'MSG_001', 'System Startup', 'Code for system startup process', 1, true,
        'admin', 'admin'),
       ('TEST_CODE_002', 'TEST_GROUP_001', 'MSG_002', 'System Shutdown', 'Code for system shutdown process', 2, true,
        'admin', 'admin'),
       ('TEST_CODE_003', 'TEST_GROUP_002', 'MSG_003', 'User Registration', 'Code for user registration process', 1,
        true, 'admin', 'admin'),
       ('TEST_CODE_004', 'TEST_GROUP_002', 'MSG_004', 'User Deactivation', 'Code for user deactivation process', 2,
        true, 'admin', 'admin'),
       ('TEST_CODE_005', 'TEST_GROUP_003', 'MSG_005', 'Email Notification', 'Code for email notifications', 1, true,
        'admin', 'admin'),
       ('TEST_CODE_006', 'TEST_GROUP_003', 'MSG_006', 'SMS Notification', 'Code for SMS notifications', 2, true,
        'admin', 'admin');

INSERT INTO menu (parent_menu_id, message_id, url, name, is_active, is_delete, sort_order, description, level,
                  create_member_id, created_at, update_member_id, updated_at)
VALUES (null, 'menu.rlse', '/rlse', '배포관리', true, false, 1, '배포관리', 1, 'admin', now(), 'admin', now()),
       (null, 'menu.master', '/master', '기준정보관리', true, false, 2, '기준정보관리', 1, 'admin', now(), 'admin', now()),
       (null, 'menu.approve', '/approve', '결재관리', true, false, 3, '결재관리', 1, 'admin', now(), 'admin', now()),
       (null, 'menu.report', '/report', '통계', true, false, 4, '통계', 1, 'admin', now(), 'admin', now()),
       (null, 'menu.system', '/system', '시스템관리', true, false, 5, '시스템관리', 1, 'admin', now(), 'admin', now()),
       (1, 'menu.rlse.master', '/rlse/master', '기준정보관리', true, false, 1, '기준정보관리', 2, 'admin', now(), 'admin', now()),
       (1, 'menu.rlse.run', '/rlse/run', '배포실행관리', true, false, 2, '배포실행관리', 2, 'admin', now(), 'admin', now()),
       (1, 'menu.rlse.temp', '/rlse/temp', '배포 임시저장 관리', true, false, 3, '배포 임시저장 관리', 2, 'admin', now(), 'admin', now()),
       (2, 'menu.master.01', '/master/01', '기준정보관리 01', true, false, 1, '기준정보관리 01', 2, 'admin', now(), 'admin', now()),
       (9, 'menu.master.01.01', '/master/01/01', '기준정보관리 01-01', true, false, 1, '기준정보관리 01-01', 3, 'admin', now(), 'admin', now()),
       (9, 'menu.master.01.02', '/master/01/02', '기준정보관리 01-02', true, false, 2, '기준정보관리 01-02', 3, 'admin', now(), 'admin', now()),
       (3, 'menu.approve.01', '/approve/01', '결재관리 01', true, false, 1, '결재관리 01', 2, 'admin', now(), 'admin', now()),
       (4, 'menu.report.01', '/report/01', '통계 01', true, false, 1, '통계 01', 2, 'admin', now(), 'admin', now()),
       (5, 'menu.code', '/system/code', '코드관리', true, false, 1, '코드관리', 2, 'admin', now(), 'admin', now()),
       (5, 'menu.message', '/system/message', '메시지관리', true, false, 2, '메시지관리', 2, 'admin', now(), 'admin', now()),
       (5, 'menu.menu', '/system/menu', '메뉴관리', true, false, 3, '메뉴관리', 2, 'admin', now(), 'admin', now()),
       (5, 'menu.auth', '/system/role', '권한관리', true, false, 4, '권한관리', 2, 'admin', now(), 'admin', now()),
       (17, 'menu.role', '/system/role', '역할관리', true, false, 4, '역할관리', 3, 'admin', now(), 'admin', now()),
       (17, 'menu.role.user', '/system/role/member', '역할별 사용자 관리', true, false, 4, '역할별 사용자 관리', 3, 'admin', now(), 'admin', now()),
       (17, 'menu.role.menu', '/system/role/menu', '역할별 메뉴 관리', true, false, 4, '역할별 메뉴 관리', 3, 'admin', now(), 'admin', now()),
       (5, 'menu.log', '/system/log', '로그관리', true, false, 5, '로그관리', 2, 'admin', now(), 'admin', now()),
       (21, 'menu.log.access', '/system/log/access', '접근로그 관리', true, false, 1, '접근로그 관리', 3, 'admin', now(), 'admin', now()),
       (21, 'menu.log.login', '/system/log/login', '로그인로그 관리', true, false, 2, '로그인로그 관리', 3, 'admin', now(), 'admin', now()),
	   (null, 'menu.user', '/user', '사용자', true, false, 6, '사용자', 1, 'admin', now(), 'admin', now()),
       (24, 'menu.user.profile', '/user/profile', '사용자 프로필', true, false, 1, '사용자 프로필', 2, 'admin', now(), 'admin', now()),
       (24, 'menu.user.password', '/user/pw-edit', '패드워드 변경', true, false, 2, '패드워드 변경', 2, 'admin', now(), 'admin', now()),
       (5, 'menu.member', '/system/members', '사용자 관리', true, false, 6, '사용자 관리', 2, 'admin', now(), 'admin', now()),
       (null, 'menu.board', '/board', '공지사항', true, false, 7, '공지사항', 1, 'admin', now(), 'admin', now());

INSERT INTO role (id, name, description, is_active, level, created_at, updated_at)
VALUES ('ROLE_ADMIN', '관리자', '시스템 관리자', true, 800, now(), now()),
       ('ROLE_USER', '사용자', '시스템 사용자', true, 10, now(), now());

INSERT INTO log_access (member_id, request_url, request_method, request_payload, request_ip, response_status,
                        response_body, user_agent, created_at)
VALUES ('userId', 'http://localhost:8080/api/roles', 'getRoles', '[]', '127.0.0.1', 200,
        'RoleResponse.RoleListResponse(roleInfos=[RoleInfo.Main(id=ROLE_ADMIN, name=관리자, description=시스템 관리자, isActive=true), RoleInfo.Main(id=ROLE_USER, name=사용자, description=시스템 사용자, isActive=true)])',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36',
        now()),
       ('userId', 'http://localhost:8080/api/menus', 'getMenus', '[true]', '127.0.0.1', 200,
        'com.evolve.evolvebe.menu.interfaces.MenuResponse$MenuList@44c85dc9',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36',
        now());

insert into role_menu (role_id, menu_id)
values  ( 'ROLE_ADMIN', 1),
        ( 'ROLE_ADMIN', 2),
        ( 'ROLE_ADMIN', 3),
        ( 'ROLE_ADMIN', 4),
        ( 'ROLE_ADMIN', 5),
        ( 'ROLE_ADMIN', 6),
        ( 'ROLE_ADMIN', 7),
        ( 'ROLE_ADMIN', 8),
        ( 'ROLE_ADMIN', 9),
        ( 'ROLE_ADMIN', 10),
        ( 'ROLE_ADMIN', 11),
        ( 'ROLE_ADMIN', 12),
        ( 'ROLE_ADMIN', 13),
        ( 'ROLE_ADMIN', 14),
        ( 'ROLE_ADMIN', 15),
        ( 'ROLE_ADMIN', 16),
        ( 'ROLE_ADMIN', 17),
        ( 'ROLE_ADMIN', 18),
        ( 'ROLE_ADMIN', 19),
        ( 'ROLE_ADMIN', 20),
        ( 'ROLE_ADMIN', 21),
        ( 'ROLE_ADMIN', 22),
        ( 'ROLE_ADMIN', 23),
        ( 'ROLE_ADMIN', 24),
        ( 'ROLE_ADMIN', 25),
        ( 'ROLE_ADMIN', 26),
        ( 'ROLE_ADMIN', 27),
        ( 'ROLE_ADMIN', 28);
