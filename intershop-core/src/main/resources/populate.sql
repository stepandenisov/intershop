insert into users (username, password, roles)
values ('admin',
        '{bcrypt}$2a$10$NbGLod52zPCH.Hb2m0W0yOihG0.mgNU//jaQwSPWC9oYubgRmwGN6', -- пароль: 'password'
        'USER,ADMIN'),
       ('user',
        '{bcrypt}$2a$10$NbGLod52zPCH.Hb2m0W0yOihG0.mgNU//jaQwSPWC9oYubgRmwGN6', -- пароль: 'password'
        'USER');

insert into carts(user_id, total)
values (1,
        0.0),
       (2,
        0.0);