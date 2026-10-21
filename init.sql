DO
$$
    BEGIN
        IF NOT EXISTS (
            SELECT FROM pg_database WHERE datname = 'evolve_db'
        ) THEN
            CREATE DATABASE evolve_db;
        END IF;
    END
$$;
