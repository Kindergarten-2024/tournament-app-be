-- public.flyway_schema_history definition

-- Drop table

-- DROP TABLE public.flyway_schema_history;

CREATE TABLE public.flyway_schema_history (
                                              installed_rank int4 NOT NULL,
                                              "version" varchar(50) NULL,
                                              description varchar(200) NOT NULL,
                                              "type" varchar(20) NOT NULL,
                                              script varchar(1000) NOT NULL,
                                              checksum int4 NULL,
                                              installed_by varchar(100) NOT NULL,
                                              installed_on timestamp NOT NULL DEFAULT now(),
                                              execution_time int4 NOT NULL,
                                              success bool NOT NULL,
                                              CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank)
);
CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


-- public.questions definition

-- Drop table

-- DROP TABLE public.questions;

CREATE TABLE public.questions (
                                  question_id bigserial NOT NULL,
                                  correct_answer varchar(255) NULL,
                                  current_question bool NULL,
                                  question varchar(255) NULL,
                                  question_order int4 NULL,
                                  time_sent varchar(255) NULL,
                                  CONSTRAINT questions_pkey PRIMARY KEY (question_id)
);


-- public.registrations_time definition

-- Drop table

-- DROP TABLE public.registrations_time;

CREATE TABLE public.registrations_time (
                                           registrations_time_id bigserial NOT NULL,
                                           registrations_end_time timestamp(6) NULL,
                                           registrations_open bool NOT NULL,
                                           tournament_round int4 NOT NULL,
                                           CONSTRAINT registrations_time_pkey PRIMARY KEY (registrations_time_id)
);


-- public.users definition

-- Drop table

-- DROP TABLE public.users;

CREATE TABLE public.users (
                              user_id bigserial NOT NULL,
                              avatar_url varchar(255) NULL,
                              full_name varchar(255) NULL,
                              registered bool NULL,
                              score int4 NOT NULL,
                              username varchar(255) NULL,
                              CONSTRAINT users_pkey PRIMARY KEY (user_id)
);


-- public.question_options definition

-- Drop table

-- DROP TABLE public.question_options;

CREATE TABLE public.question_options (
                                         question_question_id int8 NOT NULL,
                                         "options" varchar(255) NULL,
                                         CONSTRAINT fkeuommpyl8rplg04cp24a12we6 FOREIGN KEY (question_question_id) REFERENCES public.questions(question_id)
);


-- public.user_answers definition

-- Drop table

-- DROP TABLE public.user_answers;

CREATE TABLE public.user_answers (
                                     user_answer_id bigserial NOT NULL,
                                     answer varchar(255) NULL,
                                     is_correct bool NOT NULL,
                                     question_id int8 NULL,
                                     user_id int8 NULL,
                                     CONSTRAINT user_answers_pkey PRIMARY KEY (user_answer_id),
                                     CONSTRAINT fk6b46l4bb7a6wfxvmn6l7ig8vo FOREIGN KEY (question_id) REFERENCES public.questions(question_id),
                                     CONSTRAINT fkk4u357ronsopa0vqf16deuxbt FOREIGN KEY (user_id) REFERENCES public.users(user_id)
);