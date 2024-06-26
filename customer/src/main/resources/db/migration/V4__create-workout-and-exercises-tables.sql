create table icon(
    id uuid primary key,
    name varchar(255),
    url text
);

create table workout (
    id uuid primary key,
    name varchar(128),
    icon_id uuid,
    is_deleted boolean not null default false,
    created_by_user uuid not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),
    deleted_at timestamp not null default now(),

    foreign key (created_by_user) references customer_user(id),
    foreign key (icon_id) references icon(id)
);

create table user_workout(
    customer_user_id uuid,
    workout_id uuid,

    primary key (customer_user_id, workout_id),
    foreign key (customer_user_id) references customer_user(id),
    foreign key (workout_id) references workout(id)
);

create table workout_performed(
    id uuid primary key,
    workout_id uuid not null,
    start_time timestamp,
    end_time timestamp,
    performed_by_user uuid not null,

    foreign key (workout_id) references workout(id),
    foreign key (performed_by_user) references customer_user(id),
    constraint unique_workout_performed unique (workout_id, start_time, end_time, performed_by_user)
);

create table exercise(
    id uuid primary key,
    name varchar(255),
    is_deleted boolean not null default false,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),
    deleted_at timestamp not null default now()
);

create table workout_exercise(
    workout_id uuid not null,
    exercise_id uuid not null,
    series smallint,
    rep_goals smallint[],
    weight_goals numeric(5,1)[],

    foreign key (workout_id) references workout(id),
    foreign key (exercise_id) references exercise(id)
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
    foreign key (exercise_id) references exercise(id),
    foreign key (workout_performed_id) references workout_performed(id)
);

