
ALTER TABLE bowling_club.player ADD CONSTRAINT uc_name UNIQUE (name);

INSERT INTO bowling_club.player (name)
VALUES ('Tunyboy'), ('Sunny'), ('Lucky');

INSERT INTO bowling_club.member_role (name)
VALUES ('EMPLOYEE'), ('PLAYER'), ('PLATFORM_OWNER');
