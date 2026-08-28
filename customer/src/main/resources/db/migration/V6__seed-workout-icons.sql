-- Fixed ids so the mobile app can ship a matching glyph/palette catalog
-- (see src/features/workouts/icon-catalog.ts in the fe-train-up repo).
-- `url` stays null until real image assets exist; the client renders the glyph
-- from the icon name.
insert into icon (id, name, url) values
    ('11111111-1111-4111-8111-000000000001', 'barbell-orange', null),
    ('11111111-1111-4111-8111-000000000002', 'barbell-dark', null),
    ('11111111-1111-4111-8111-000000000003', 'barbell-light', null),
    ('11111111-1111-4111-8111-000000000004', 'fitness-orange', null),
    ('11111111-1111-4111-8111-000000000005', 'body-dark', null),
    ('11111111-1111-4111-8111-000000000006', 'bicycle-orange', null),
    ('11111111-1111-4111-8111-000000000007', 'walk-light', null),
    ('11111111-1111-4111-8111-000000000008', 'flame-dark', null)
on conflict (id) do nothing;
