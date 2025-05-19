-- Crée l’index insensible à la casse
CREATE INDEX IF NOT EXISTS idx_movie_title_lower ON movie (LOWER(title));

-- Active l’extension pg_trgm (à exécuter une seule fois sur la base)
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Crée l’index trigramme pour les recherches LIKE 'mot%'
CREATE INDEX IF NOT EXISTS idx_movie_title_trgm ON movie USING gin (LOWER(title) gin_trgm_ops);