-- 가용시간으로 생성한 코스의 종료 시각은 입력되지 않은 상태로 유지한다.
ALTER TABLE course ALTER COLUMN end_at DROP NOT NULL;
ALTER TABLE course ADD COLUMN duration_minutes INTEGER;
ALTER TABLE course ADD CONSTRAINT ck_course_duration_minutes
    CHECK (duration_minutes IS NULL OR duration_minutes > 0);

INSERT INTO theme (name)
SELECT names.name
FROM (VALUES
    ('조용함'), ('로맨틱'), ('아늑함'), ('감성적'), ('활기참'),
    ('이색적'), ('여유로움'), ('고급스러움'), ('레트로'), ('자연친화적')
) AS names(name)
WHERE NOT EXISTS (SELECT 1 FROM theme WHERE theme.name = names.name);
