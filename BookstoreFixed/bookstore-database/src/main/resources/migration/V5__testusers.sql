
INSERT INTO users (username, password, role, enabled) 
VALUES ('admin', 'admin123', 'ADMIN', true)
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, role, enabled) 
VALUES ('user', 'user123', 'USER', true)
ON CONFLICT (username) DO NOTHING;