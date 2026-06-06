-- Create profiles table
CREATE TABLE IF NOT EXISTS profiles (
    id TEXT PRIMARY KEY,
    email TEXT,
    display_name TEXT,
    xp INTEGER DEFAULT 0,
    level INTEGER DEFAULT 1,
    streak INTEGER DEFAULT 0,
    wins INTEGER DEFAULT 0,
    avatar_url TEXT,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- Create level_progress table
CREATE TABLE IF NOT EXISTS level_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id TEXT REFERENCES profiles(id) ON DELETE CASCADE NOT NULL,
    language TEXT NOT NULL,
    track TEXT NOT NULL,
    level_id INTEGER NOT NULL,
    stars INTEGER DEFAULT 0,
    completed BOOLEAN DEFAULT false,
    completed_at TIMESTAMPTZ,
    UNIQUE(user_id, language, track, level_id)
);

-- Create user_learning_profile table
CREATE TABLE IF NOT EXISTS user_learning_profile (
    id BIGSERIAL PRIMARY KEY,
    user_id TEXT REFERENCES profiles(id) ON DELETE CASCADE NOT NULL,
    language TEXT NOT NULL,
    track TEXT DEFAULT 'beginner',
    level_id INTEGER NOT NULL,
    attempts INTEGER DEFAULT 0,
    failed_commands TEXT,
    last_error TEXT,
    updated_at TIMESTAMPTZ DEFAULT now(),
    UNIQUE(user_id, language, track, level_id)
);

-- Create xp_history table
CREATE TABLE IF NOT EXISTS xp_history (
    id BIGSERIAL PRIMARY KEY,
    user_id TEXT REFERENCES profiles(id) ON DELETE CASCADE NOT NULL,
    amount INTEGER NOT NULL,
    reason TEXT,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Create achievements table (for future use)
CREATE TABLE IF NOT EXISTS achievements (
    id BIGSERIAL PRIMARY KEY,
    user_id TEXT REFERENCES profiles(id) ON DELETE CASCADE NOT NULL,
    achievement_id TEXT NOT NULL,
    unlocked_at TIMESTAMPTZ DEFAULT now()
);

-- Create daily_challenges table (for future use)
CREATE TABLE IF NOT EXISTS daily_challenges (
    id BIGSERIAL PRIMARY KEY,
    user_id TEXT REFERENCES profiles(id) ON DELETE CASCADE NOT NULL,
    challenge_id TEXT NOT NULL,
    completed BOOLEAN DEFAULT false,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Create user_activity table (for future use)
CREATE TABLE IF NOT EXISTS user_activity (
    id BIGSERIAL PRIMARY KEY,
    user_id TEXT REFERENCES profiles(id) ON DELETE CASCADE NOT NULL,
    activity_type TEXT NOT NULL,
    activity_date DATE NOT NULL,
    data JSONB,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Create friends table (for future use)
CREATE TABLE IF NOT EXISTS friends (
    id BIGSERIAL PRIMARY KEY,
    user_id TEXT REFERENCES profiles(id) ON DELETE CASCADE NOT NULL,
    friend_id TEXT REFERENCES profiles(id) ON DELETE CASCADE NOT NULL,
    status TEXT DEFAULT 'active',
    created_at TIMESTAMPTZ DEFAULT now(),
    UNIQUE(user_id, friend_id)
);

-- Create indexes for performance
CREATE INDEX idx_level_progress_user_id ON level_progress(user_id);
CREATE INDEX idx_learning_profile_user_id ON user_learning_profile(user_id);
CREATE INDEX idx_xp_history_user_id ON xp_history(user_id);
CREATE INDEX idx_profiles_xp ON profiles(xp DESC);
CREATE INDEX idx_profiles_level ON profiles(level DESC);

