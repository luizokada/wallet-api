-- Guardamos só o caminho do objeto no bucket (ex.: avatars/{userId}/{uuid}.png).
-- A URL pública é montada na resposta, então trocar de CDN/provider não exige
-- reescrever os dados já salvos.
ALTER TABLE users RENAME COLUMN avatar_url TO avatar_path;
