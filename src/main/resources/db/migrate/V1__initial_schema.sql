--
-- PostgreSQL database dump
--

-- Dumped from database version 15.3
-- Dumped by pg_dump version 15.3

-- Started on 2024-01-18 14:18:21

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 5 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: pg_database_owner
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO pg_database_owner;

--
-- TOC entry 3364 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: pg_database_owner
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 215 (class 1259 OID 139081)
-- Name: question_options; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.question_options (
    question_question_id bigint NOT NULL,
    options character varying(255)
);


ALTER TABLE public.question_options OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 139085)
-- Name: questions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.questions (
    question_id bigint NOT NULL,
    correct_answer character varying(255),
    difficulty integer,
    question character varying(255)
);


ALTER TABLE public.questions OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 139084)
-- Name: questions_question_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.questions_question_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.questions_question_id_seq OWNER TO postgres;

--
-- TOC entry 3365 (class 0 OID 0)
-- Dependencies: 216
-- Name: questions_question_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.questions_question_id_seq OWNED BY public.questions.question_id;


--
-- TOC entry 219 (class 1259 OID 139094)
-- Name: registrations_time; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.registrations_time (
    registrations_time_id bigint NOT NULL,
    registrations_end_time timestamp(6) without time zone,
    registrations_open boolean NOT NULL,
    tournament_round integer NOT NULL
);


ALTER TABLE public.registrations_time OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 139093)
-- Name: registrations_time_registrations_time_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.registrations_time_registrations_time_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.registrations_time_registrations_time_id_seq OWNER TO postgres;

--
-- TOC entry 3366 (class 0 OID 0)
-- Dependencies: 218
-- Name: registrations_time_registrations_time_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.registrations_time_registrations_time_id_seq OWNED BY public.registrations_time.registrations_time_id;


--
-- TOC entry 221 (class 1259 OID 139101)
-- Name: user_answers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_answers (
    user_answer_id bigint NOT NULL,
    answer character varying(255),
    is_correct boolean NOT NULL,
    question_id bigint,
    user_id bigint
);


ALTER TABLE public.user_answers OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 139100)
-- Name: user_answers_user_answer_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_answers_user_answer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_answers_user_answer_id_seq OWNER TO postgres;

--
-- TOC entry 3367 (class 0 OID 0)
-- Dependencies: 220
-- Name: user_answers_user_answer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.user_answers_user_answer_id_seq OWNED BY public.user_answers.user_answer_id;


--
-- TOC entry 223 (class 1259 OID 139108)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    user_id bigint NOT NULL,
    avatar_url character varying(255),
    full_name character varying(255),
    registered boolean,
    score integer NOT NULL,
    username character varying(255)
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 139107)
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_user_id_seq OWNER TO postgres;

--
-- TOC entry 3368 (class 0 OID 0)
-- Dependencies: 222
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_user_id_seq OWNED BY public.users.user_id;


--
-- TOC entry 3193 (class 2604 OID 139088)
-- Name: questions question_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.questions ALTER COLUMN question_id SET DEFAULT nextval('public.questions_question_id_seq'::regclass);


--
-- TOC entry 3194 (class 2604 OID 139097)
-- Name: registrations_time registrations_time_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.registrations_time ALTER COLUMN registrations_time_id SET DEFAULT nextval('public.registrations_time_registrations_time_id_seq'::regclass);


--
-- TOC entry 3195 (class 2604 OID 139104)
-- Name: user_answers user_answer_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_answers ALTER COLUMN user_answer_id SET DEFAULT nextval('public.user_answers_user_answer_id_seq'::regclass);


--
-- TOC entry 3196 (class 2604 OID 139111)
-- Name: users user_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN user_id SET DEFAULT nextval('public.users_user_id_seq'::regclass);


--
-- TOC entry 3350 (class 0 OID 139081)
-- Dependencies: 215
-- Data for Name: question_options; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.question_options (question_question_id, options) FROM stdin;
\.


--
-- TOC entry 3352 (class 0 OID 139085)
-- Dependencies: 217
-- Data for Name: questions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.questions (question_id, correct_answer, difficulty, question) FROM stdin;
\.


--
-- TOC entry 3354 (class 0 OID 139094)
-- Dependencies: 219
-- Data for Name: registrations_time; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.registrations_time (registrations_time_id, registrations_end_time, registrations_open, tournament_round) FROM stdin;
1	2024-01-30 17:00:00	t	1
\.


--
-- TOC entry 3356 (class 0 OID 139101)
-- Dependencies: 221
-- Data for Name: user_answers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_answers (user_answer_id, answer, is_correct, question_id, user_id) FROM stdin;
\.


--
-- TOC entry 3358 (class 0 OID 139108)
-- Dependencies: 223
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (user_id, avatar_url, full_name, registered, score, username) FROM stdin;
1	https://avatars.githubusercontent.com/u/77331068?v=4	Stefanos David Georgiou	f	0	Ogstef
\.


--
-- TOC entry 3369 (class 0 OID 0)
-- Dependencies: 216
-- Name: questions_question_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.questions_question_id_seq', 1, false);


--
-- TOC entry 3370 (class 0 OID 0)
-- Dependencies: 218
-- Name: registrations_time_registrations_time_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.registrations_time_registrations_time_id_seq', 1, true);


--
-- TOC entry 3371 (class 0 OID 0)
-- Dependencies: 220
-- Name: user_answers_user_answer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_answers_user_answer_id_seq', 1, false);


--
-- TOC entry 3372 (class 0 OID 0)
-- Dependencies: 222
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_user_id_seq', 1, true);


--
-- TOC entry 3198 (class 2606 OID 139092)
-- Name: questions questions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.questions
    ADD CONSTRAINT questions_pkey PRIMARY KEY (question_id);


--
-- TOC entry 3200 (class 2606 OID 139099)
-- Name: registrations_time registrations_time_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.registrations_time
    ADD CONSTRAINT registrations_time_pkey PRIMARY KEY (registrations_time_id);


--
-- TOC entry 3202 (class 2606 OID 139106)
-- Name: user_answers user_answers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_answers
    ADD CONSTRAINT user_answers_pkey PRIMARY KEY (user_answer_id);


--
-- TOC entry 3204 (class 2606 OID 139115)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- TOC entry 3206 (class 2606 OID 139121)
-- Name: user_answers fk6b46l4bb7a6wfxvmn6l7ig8vo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_answers
    ADD CONSTRAINT fk6b46l4bb7a6wfxvmn6l7ig8vo FOREIGN KEY (question_id) REFERENCES public.questions(question_id);


--
-- TOC entry 3205 (class 2606 OID 139116)
-- Name: question_options fkeuommpyl8rplg04cp24a12we6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.question_options
    ADD CONSTRAINT fkeuommpyl8rplg04cp24a12we6 FOREIGN KEY (question_question_id) REFERENCES public.questions(question_id);


--
-- TOC entry 3207 (class 2606 OID 139126)
-- Name: user_answers fkk4u357ronsopa0vqf16deuxbt; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_answers
    ADD CONSTRAINT fkk4u357ronsopa0vqf16deuxbt FOREIGN KEY (user_id) REFERENCES public.users(user_id);


-- Completed on 2024-01-18 14:18:21

--
-- PostgreSQL database dump complete
--

