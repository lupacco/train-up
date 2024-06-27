create table icon(
    id uuid default uuid_generate_v4()  primary key,
    name varchar(255),
    url text
);

create table workout (
    id uuid default uuid_generate_v4() primary key,
    name varchar(128) not null,
    icon_id uuid,
    is_deleted boolean not null default false,
    created_by_user uuid not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),
    deleted_at timestamp ,

    foreign key (created_by_user) references customer_user(id) on delete cascade,
    foreign key (icon_id) references icon(id) on delete set null
);

create table user_workout(
    customer_user_id uuid not null,
    workout_id uuid not null,

    primary key (customer_user_id, workout_id),
    foreign key (customer_user_id) references customer_user(id) on delete cascade,
    foreign key (workout_id) references workout(id) on delete cascade
);

create table workout_performed(
    id uuid default uuid_generate_v4()  primary key,
    workout_id uuid not null,
    start_time timestamp,
    end_time timestamp,
    performed_by_user uuid not null,

    foreign key (workout_id) references workout(id) on delete cascade,
    foreign key (performed_by_user) references customer_user(id) on delete cascade,
    constraint unique_workout_performed unique (workout_id, start_time, end_time, performed_by_user)
);

create table exercise(
    id uuid default uuid_generate_v4()  primary key,
    name varchar(255) not null,
    is_deleted boolean not null default false,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),
    deleted_at timestamp
);

create table workout_exercise(
    workout_id uuid not null,
    exercise_id uuid not null,
    series smallint,
    rep_goals smallint[],
    weight_goals numeric(5,1)[],

    foreign key (workout_id) references workout(id) on delete cascade,
    foreign key (exercise_id) references exercise(id) on delete cascade
);

create table exercise_performed(
    exercise_id uuid not null,
    workout_performed_id uuid not null,
    serie smallint,
    reps_goal smallint,
    reps_performed smallint,
    weight_goal numeric(5,1),
    weigth_performed numeric(5,1),

    primary key (exercise_id, workout_performed_id, serie),
    foreign key (exercise_id) references exercise(id) on delete cascade,
    foreign key (workout_performed_id) references workout_performed(id) on delete cascade
);

