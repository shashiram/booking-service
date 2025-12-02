--
-- PostgreSQL database dump
--

\restrict Vj58gt4Esagbnw8ajKwJ8IpM9OgQQ9QJuXDiTMqv2peA6azb3GDQmq0JA396PgU

-- Dumped from database version 15.14 (Debian 15.14-1.pgdg13+1)
-- Dumped by pg_dump version 15.14

-- Started on 2025-12-02 08:24:57 UTC

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
-- TOC entry 2 (class 3079 OID 58192)
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- TOC entry 3496 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 223 (class 1259 OID 58273)
-- Name: availability_slots; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.availability_slots (
    id bigint NOT NULL,
    cleaner_id bigint NOT NULL,
    slot_date date NOT NULL,
    start_time time without time zone NOT NULL,
    end_time time without time zone NOT NULL,
    booking_id uuid,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    is_available boolean DEFAULT true
);


ALTER TABLE public.availability_slots OWNER TO admin;

--
-- TOC entry 222 (class 1259 OID 58272)
-- Name: availability_slots_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.availability_slots_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.availability_slots_id_seq OWNER TO admin;

--
-- TOC entry 3497 (class 0 OID 0)
-- Dependencies: 222
-- Name: availability_slots_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.availability_slots_id_seq OWNED BY public.availability_slots.id;


--
-- TOC entry 221 (class 1259 OID 58248)
-- Name: booking_cleaner; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.booking_cleaner (
    id bigint NOT NULL,
    booking_id uuid NOT NULL,
    cleaner_id bigint NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.booking_cleaner OWNER TO admin;

--
-- TOC entry 220 (class 1259 OID 58247)
-- Name: booking_cleaner_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.booking_cleaner_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.booking_cleaner_id_seq OWNER TO admin;

--
-- TOC entry 3498 (class 0 OID 0)
-- Dependencies: 220
-- Name: booking_cleaner_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.booking_cleaner_id_seq OWNED BY public.booking_cleaner.id;


--
-- TOC entry 219 (class 1259 OID 58236)
-- Name: bookings; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.bookings (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    customer_id bigint NOT NULL,
    duration_hours integer NOT NULL,
    total_cleaners integer NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    booking_date date NOT NULL,
    status character varying(50) NOT NULL,
    start_time time without time zone NOT NULL,
    end_time time without time zone NOT NULL,
    CONSTRAINT bookings_total_cleaners_check CHECK ((total_cleaners = ANY (ARRAY[1, 2, 3])))
);


ALTER TABLE public.bookings OWNER TO admin;

--
-- TOC entry 218 (class 1259 OID 58217)
-- Name: cleaners; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.cleaners (
    id bigint NOT NULL,
    name character varying(100) NOT NULL,
    email character varying(100) NOT NULL,
    phone character varying(20),
    vehicle_id bigint NOT NULL,
    is_active boolean DEFAULT true,
    working_hours_start time without time zone DEFAULT '08:00:00'::time without time zone NOT NULL,
    working_hours_end time without time zone DEFAULT '22:00:00'::time without time zone NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.cleaners OWNER TO admin;

--
-- TOC entry 217 (class 1259 OID 58216)
-- Name: cleaners_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.cleaners_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.cleaners_id_seq OWNER TO admin;

--
-- TOC entry 3499 (class 0 OID 0)
-- Dependencies: 217
-- Name: cleaners_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.cleaners_id_seq OWNED BY public.cleaners.id;


--
-- TOC entry 216 (class 1259 OID 58204)
-- Name: vehicles; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.vehicles (
    id bigint NOT NULL,
    vehicle_identifier character varying(50) NOT NULL,
    capacity integer DEFAULT 5 NOT NULL,
    is_active boolean DEFAULT true,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.vehicles OWNER TO admin;

--
-- TOC entry 215 (class 1259 OID 58203)
-- Name: vehicles_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.vehicles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.vehicles_id_seq OWNER TO admin;

--
-- TOC entry 3500 (class 0 OID 0)
-- Dependencies: 215
-- Name: vehicles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.vehicles_id_seq OWNED BY public.vehicles.id;


--
-- TOC entry 3306 (class 2604 OID 58276)
-- Name: availability_slots id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.availability_slots ALTER COLUMN id SET DEFAULT nextval('public.availability_slots_id_seq'::regclass);


--
-- TOC entry 3304 (class 2604 OID 58251)
-- Name: booking_cleaner id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.booking_cleaner ALTER COLUMN id SET DEFAULT nextval('public.booking_cleaner_id_seq'::regclass);


--
-- TOC entry 3295 (class 2604 OID 58220)
-- Name: cleaners id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.cleaners ALTER COLUMN id SET DEFAULT nextval('public.cleaners_id_seq'::regclass);


--
-- TOC entry 3290 (class 2604 OID 58207)
-- Name: vehicles id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.vehicles ALTER COLUMN id SET DEFAULT nextval('public.vehicles_id_seq'::regclass);


--
-- TOC entry 3337 (class 2606 OID 58281)
-- Name: availability_slots availability_slots_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.availability_slots
    ADD CONSTRAINT availability_slots_pkey PRIMARY KEY (id);


--
-- TOC entry 3331 (class 2606 OID 58254)
-- Name: booking_cleaner booking_cleaner_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.booking_cleaner
    ADD CONSTRAINT booking_cleaner_pkey PRIMARY KEY (id);


--
-- TOC entry 3322 (class 2606 OID 58246)
-- Name: bookings bookings_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.bookings
    ADD CONSTRAINT bookings_pkey PRIMARY KEY (id);


--
-- TOC entry 3316 (class 2606 OID 58230)
-- Name: cleaners cleaners_email_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.cleaners
    ADD CONSTRAINT cleaners_email_key UNIQUE (email);


--
-- TOC entry 3318 (class 2606 OID 58228)
-- Name: cleaners cleaners_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.cleaners
    ADD CONSTRAINT cleaners_pkey PRIMARY KEY (id);


--
-- TOC entry 3335 (class 2606 OID 58256)
-- Name: booking_cleaner uk_booking_cleaner_unique; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.booking_cleaner
    ADD CONSTRAINT uk_booking_cleaner_unique UNIQUE (booking_id, cleaner_id);


--
-- TOC entry 3343 (class 2606 OID 58283)
-- Name: availability_slots uk_cleaner_time_slot; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.availability_slots
    ADD CONSTRAINT uk_cleaner_time_slot UNIQUE (cleaner_id, slot_date, start_time, end_time);


--
-- TOC entry 3312 (class 2606 OID 58213)
-- Name: vehicles vehicles_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.vehicles
    ADD CONSTRAINT vehicles_pkey PRIMARY KEY (id);


--
-- TOC entry 3314 (class 2606 OID 58215)
-- Name: vehicles vehicles_vehicle_identifier_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.vehicles
    ADD CONSTRAINT vehicles_vehicle_identifier_key UNIQUE (vehicle_identifier);


--
-- TOC entry 3338 (class 1259 OID 58305)
-- Name: idx_availability_booking_id; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_availability_booking_id ON public.availability_slots USING btree (booking_id);


--
-- TOC entry 3339 (class 1259 OID 58303)
-- Name: idx_availability_cleaner_date; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_availability_cleaner_date ON public.availability_slots USING btree (cleaner_id, slot_date);


--
-- TOC entry 3340 (class 1259 OID 58308)
-- Name: idx_availability_date_status; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_availability_date_status ON public.availability_slots USING btree (slot_date, is_available);


--
-- TOC entry 3341 (class 1259 OID 58306)
-- Name: idx_availability_time_range; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_availability_time_range ON public.availability_slots USING btree (slot_date, start_time, end_time);


--
-- TOC entry 3332 (class 1259 OID 58300)
-- Name: idx_booking_cleaner_booking_id; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_booking_cleaner_booking_id ON public.booking_cleaner USING btree (booking_id);


--
-- TOC entry 3333 (class 1259 OID 58301)
-- Name: idx_booking_cleaner_cleaner_id; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_booking_cleaner_cleaner_id ON public.booking_cleaner USING btree (cleaner_id);


--
-- TOC entry 3323 (class 1259 OID 58313)
-- Name: idx_bookings_booking_date; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_bookings_booking_date ON public.bookings USING btree (booking_date);


--
-- TOC entry 3324 (class 1259 OID 58299)
-- Name: idx_bookings_created_at; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_bookings_created_at ON public.bookings USING btree (created_at);


--
-- TOC entry 3325 (class 1259 OID 58310)
-- Name: idx_bookings_customer_id; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_bookings_customer_id ON public.bookings USING btree (customer_id);


--
-- TOC entry 3326 (class 1259 OID 58312)
-- Name: idx_bookings_end_time; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_bookings_end_time ON public.bookings USING btree (end_time);


--
-- TOC entry 3327 (class 1259 OID 58296)
-- Name: idx_bookings_id; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_bookings_id ON public.bookings USING btree (id);


--
-- TOC entry 3328 (class 1259 OID 58311)
-- Name: idx_bookings_start_time; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_bookings_start_time ON public.bookings USING btree (start_time);


--
-- TOC entry 3329 (class 1259 OID 58309)
-- Name: idx_bookings_status; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_bookings_status ON public.bookings USING btree (status);


--
-- TOC entry 3319 (class 1259 OID 58295)
-- Name: idx_cleaners_active; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_cleaners_active ON public.cleaners USING btree (is_active) WHERE (is_active = true);


--
-- TOC entry 3320 (class 1259 OID 58294)
-- Name: idx_cleaners_vehicle_id; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_cleaners_vehicle_id ON public.cleaners USING btree (vehicle_id);


--
-- TOC entry 3347 (class 2606 OID 58289)
-- Name: availability_slots fk_availability_booking; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.availability_slots
    ADD CONSTRAINT fk_availability_booking FOREIGN KEY (booking_id) REFERENCES public.bookings(id) ON DELETE SET NULL;


--
-- TOC entry 3348 (class 2606 OID 58284)
-- Name: availability_slots fk_availability_cleaner; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.availability_slots
    ADD CONSTRAINT fk_availability_cleaner FOREIGN KEY (cleaner_id) REFERENCES public.cleaners(id) ON DELETE CASCADE;


--
-- TOC entry 3345 (class 2606 OID 58257)
-- Name: booking_cleaner fk_booking_cleaner_booking; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.booking_cleaner
    ADD CONSTRAINT fk_booking_cleaner_booking FOREIGN KEY (booking_id) REFERENCES public.bookings(id) ON DELETE CASCADE;


--
-- TOC entry 3346 (class 2606 OID 58262)
-- Name: booking_cleaner fk_booking_cleaner_cleaner; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.booking_cleaner
    ADD CONSTRAINT fk_booking_cleaner_cleaner FOREIGN KEY (cleaner_id) REFERENCES public.cleaners(id) ON DELETE CASCADE;


--
-- TOC entry 3344 (class 2606 OID 58231)
-- Name: cleaners fk_cleaner_vehicle; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.cleaners
    ADD CONSTRAINT fk_cleaner_vehicle FOREIGN KEY (vehicle_id) REFERENCES public.vehicles(id) ON DELETE RESTRICT;


-- Completed on 2025-12-02 08:24:57 UTC

--
-- PostgreSQL database dump complete
--

\unrestrict Vj58gt4Esagbnw8ajKwJ8IpM9OgQQ9QJuXDiTMqv2peA6azb3GDQmq0JA396PgU

