--
-- PostgreSQL database dump
--

-- Dumped from database version 15.8
-- Dumped by pg_dump version 16.4

-- Started on 2024-09-18 22:07:48

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 215 (class 1259 OID 16750)
-- Name: clientes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.clientes (
    id_cliente bigint NOT NULL,
    nome character varying(50) NOT NULL,
    email character varying(50) NOT NULL,
    telefone character varying(11) NOT NULL,
    cpf character varying(11) NOT NULL
);


ALTER TABLE public.clientes OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 16749)
-- Name: clientes_id_cliente_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.clientes ALTER COLUMN id_cliente ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.clientes_id_cliente_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 222 (class 1259 OID 16784)
-- Name: concessao; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.concessao (
    id_livro bigint NOT NULL,
    id_emprestimo bigint NOT NULL
);


ALTER TABLE public.concessao OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16773)
-- Name: emprestimos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.emprestimos (
    id_emprestimo bigint NOT NULL,
    data_emprestimo date DEFAULT CURRENT_DATE NOT NULL,
    data_devolucao date NOT NULL,
    valor_emprestimo numeric(10,2) NOT NULL,
    status integer NOT NULL,
    id_cliente bigint NOT NULL
);


ALTER TABLE public.emprestimos OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16772)
-- Name: emprestimos_id_emprestimo_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.emprestimos ALTER COLUMN id_emprestimo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.emprestimos_id_emprestimo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 219 (class 1259 OID 16762)
-- Name: livros; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.livros (
    id_livro bigint NOT NULL,
    titulo character varying(100) NOT NULL,
    data_publicacao date NOT NULL,
    quantidade_disponivel integer NOT NULL,
    quantidade_total integer NOT NULL,
    valor numeric(10,2) NOT NULL,
    nome_autor character varying(100) NOT NULL,
    id_sessao bigint
);


ALTER TABLE public.livros OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 16761)
-- Name: livros_id_livro_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.livros ALTER COLUMN id_livro ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.livros_id_livro_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 224 (class 1259 OID 16800)
-- Name: pagamentos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pagamentos (
    id_pagamento bigint NOT NULL,
    valor_pago numeric(10,2) NOT NULL,
    status integer NOT NULL,
    id_emprestimo bigint NOT NULL
);


ALTER TABLE public.pagamentos OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16799)
-- Name: pagamentos_id_pagamento_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.pagamentos ALTER COLUMN id_pagamento ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.pagamentos_id_pagamento_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 217 (class 1259 OID 16756)
-- Name: sessoes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sessoes (
    id_sessao bigint NOT NULL,
    codigo integer NOT NULL,
    nome character varying(50) NOT NULL
);


ALTER TABLE public.sessoes OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 16755)
-- Name: sessoes_id_sessao_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.sessoes ALTER COLUMN id_sessao ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.sessoes_id_sessao_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 3358 (class 0 OID 16750)
-- Dependencies: 215
-- Data for Name: clientes; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.clientes (id_cliente, nome, email, telefone, cpf) OVERRIDING SYSTEM VALUE VALUES (1, 'Jose Marcos', 'josemar@gmail.com', '4940028922', '12345678900');
INSERT INTO public.clientes (id_cliente, nome, email, telefone, cpf) OVERRIDING SYSTEM VALUE VALUES (2, 'Maria Fernanda', 'mfernanda@gmail.com', '47999999999', '15973545655');


--
-- TOC entry 3365 (class 0 OID 16784)
-- Dependencies: 222
-- Data for Name: concessao; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.concessao (id_livro, id_emprestimo) VALUES (2, 1);
INSERT INTO public.concessao (id_livro, id_emprestimo) VALUES (3, 2);
INSERT INTO public.concessao (id_livro, id_emprestimo) VALUES (1, 2);
INSERT INTO public.concessao (id_livro, id_emprestimo) VALUES (2, 3);
INSERT INTO public.concessao (id_livro, id_emprestimo) VALUES (1, 3);
INSERT INTO public.concessao (id_livro, id_emprestimo) VALUES (3, 4);
INSERT INTO public.concessao (id_livro, id_emprestimo) VALUES (3, 5);
INSERT INTO public.concessao (id_livro, id_emprestimo) VALUES (3, 6);


--
-- TOC entry 3364 (class 0 OID 16773)
-- Dependencies: 221
-- Data for Name: emprestimos; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.emprestimos (id_emprestimo, data_emprestimo, data_devolucao, valor_emprestimo, status, id_cliente) OVERRIDING SYSTEM VALUE VALUES (1, '2024-09-18', '2024-09-29', 45.50, 1, 2);
INSERT INTO public.emprestimos (id_emprestimo, data_emprestimo, data_devolucao, valor_emprestimo, status, id_cliente) OVERRIDING SYSTEM VALUE VALUES (2, '2024-09-18', '2024-09-19', 75.80, 1, 1);
INSERT INTO public.emprestimos (id_emprestimo, data_emprestimo, data_devolucao, valor_emprestimo, status, id_cliente) OVERRIDING SYSTEM VALUE VALUES (3, '2024-09-18', '2024-09-22', 105.40, 1, 1);
INSERT INTO public.emprestimos (id_emprestimo, data_emprestimo, data_devolucao, valor_emprestimo, status, id_cliente) OVERRIDING SYSTEM VALUE VALUES (4, '2024-09-18', '2024-10-15', 15.90, 1, 2);
INSERT INTO public.emprestimos (id_emprestimo, data_emprestimo, data_devolucao, valor_emprestimo, status, id_cliente) OVERRIDING SYSTEM VALUE VALUES (5, '2024-09-18', '2024-09-29', 15.90, 1, 2);
INSERT INTO public.emprestimos (id_emprestimo, data_emprestimo, data_devolucao, valor_emprestimo, status, id_cliente) OVERRIDING SYSTEM VALUE VALUES (6, '2024-09-18', '2024-09-29', 15.90, 1, 2);


--
-- TOC entry 3362 (class 0 OID 16762)
-- Dependencies: 219
-- Data for Name: livros; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.livros (id_livro, titulo, data_publicacao, quantidade_disponivel, quantidade_total, valor, nome_autor, id_sessao) OVERRIDING SYSTEM VALUE VALUES (2, 'A Biblioteca da Meia-Noite', '2021-09-27', 13, 15, 45.50, 'Matt Haig', NULL);
INSERT INTO public.livros (id_livro, titulo, data_publicacao, quantidade_disponivel, quantidade_total, valor, nome_autor, id_sessao) OVERRIDING SYSTEM VALUE VALUES (1, 'Amigo Imaginario', '2020-10-02', 13, 15, 59.90, 'Stephen Chbosky', 1);
INSERT INTO public.livros (id_livro, titulo, data_publicacao, quantidade_disponivel, quantidade_total, valor, nome_autor, id_sessao) OVERRIDING SYSTEM VALUE VALUES (3, 'O sol é para todos', '2006-10-10', 11, 20, 15.90, 'Harper Lee', 3);


--
-- TOC entry 3367 (class 0 OID 16800)
-- Dependencies: 224
-- Data for Name: pagamentos; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.pagamentos (id_pagamento, valor_pago, status, id_emprestimo) OVERRIDING SYSTEM VALUE VALUES (1, 45.50, 2, 1);
INSERT INTO public.pagamentos (id_pagamento, valor_pago, status, id_emprestimo) OVERRIDING SYSTEM VALUE VALUES (2, 10.00, 1, 2);
INSERT INTO public.pagamentos (id_pagamento, valor_pago, status, id_emprestimo) OVERRIDING SYSTEM VALUE VALUES (3, 7.00, 1, 4);
INSERT INTO public.pagamentos (id_pagamento, valor_pago, status, id_emprestimo) OVERRIDING SYSTEM VALUE VALUES (4, 15.90, 2, 6);


--
-- TOC entry 3360 (class 0 OID 16756)
-- Dependencies: 217
-- Data for Name: sessoes; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.sessoes (id_sessao, codigo, nome) OVERRIDING SYSTEM VALUE VALUES (1, 1, 'Terror');
INSERT INTO public.sessoes (id_sessao, codigo, nome) OVERRIDING SYSTEM VALUE VALUES (2, 2, 'Romance');
INSERT INTO public.sessoes (id_sessao, codigo, nome) OVERRIDING SYSTEM VALUE VALUES (3, 3, 'Ficção Cientifica');


--
-- TOC entry 3373 (class 0 OID 0)
-- Dependencies: 214
-- Name: clientes_id_cliente_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.clientes_id_cliente_seq', 2, true);


--
-- TOC entry 3374 (class 0 OID 0)
-- Dependencies: 220
-- Name: emprestimos_id_emprestimo_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.emprestimos_id_emprestimo_seq', 6, true);


--
-- TOC entry 3375 (class 0 OID 0)
-- Dependencies: 218
-- Name: livros_id_livro_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.livros_id_livro_seq', 3, true);


--
-- TOC entry 3376 (class 0 OID 0)
-- Dependencies: 223
-- Name: pagamentos_id_pagamento_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pagamentos_id_pagamento_seq', 4, true);


--
-- TOC entry 3377 (class 0 OID 0)
-- Dependencies: 216
-- Name: sessoes_id_sessao_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.sessoes_id_sessao_seq', 3, true);


--
-- TOC entry 3199 (class 2606 OID 16754)
-- Name: clientes clientes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clientes
    ADD CONSTRAINT clientes_pkey PRIMARY KEY (id_cliente);


--
-- TOC entry 3207 (class 2606 OID 16788)
-- Name: concessao concessao_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.concessao
    ADD CONSTRAINT concessao_pkey PRIMARY KEY (id_livro, id_emprestimo);


--
-- TOC entry 3205 (class 2606 OID 16778)
-- Name: emprestimos emprestimos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.emprestimos
    ADD CONSTRAINT emprestimos_pkey PRIMARY KEY (id_emprestimo);


--
-- TOC entry 3203 (class 2606 OID 16766)
-- Name: livros livros_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.livros
    ADD CONSTRAINT livros_pkey PRIMARY KEY (id_livro);


--
-- TOC entry 3209 (class 2606 OID 16804)
-- Name: pagamentos pagamentos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pagamentos
    ADD CONSTRAINT pagamentos_pkey PRIMARY KEY (id_pagamento);


--
-- TOC entry 3201 (class 2606 OID 16760)
-- Name: sessoes sessoes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sessoes
    ADD CONSTRAINT sessoes_pkey PRIMARY KEY (id_sessao);


--
-- TOC entry 3212 (class 2606 OID 16794)
-- Name: concessao concessao_id_emprestimo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.concessao
    ADD CONSTRAINT concessao_id_emprestimo_fkey FOREIGN KEY (id_emprestimo) REFERENCES public.emprestimos(id_emprestimo);


--
-- TOC entry 3213 (class 2606 OID 16789)
-- Name: concessao concessao_id_livro_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.concessao
    ADD CONSTRAINT concessao_id_livro_fkey FOREIGN KEY (id_livro) REFERENCES public.livros(id_livro);


--
-- TOC entry 3211 (class 2606 OID 16779)
-- Name: emprestimos emprestimos_id_cliente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.emprestimos
    ADD CONSTRAINT emprestimos_id_cliente_fkey FOREIGN KEY (id_cliente) REFERENCES public.clientes(id_cliente);


--
-- TOC entry 3210 (class 2606 OID 16767)
-- Name: livros livros_id_sessao_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.livros
    ADD CONSTRAINT livros_id_sessao_fkey FOREIGN KEY (id_sessao) REFERENCES public.sessoes(id_sessao);


--
-- TOC entry 3214 (class 2606 OID 16805)
-- Name: pagamentos pagamentos_id_emprestimo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pagamentos
    ADD CONSTRAINT pagamentos_id_emprestimo_fkey FOREIGN KEY (id_emprestimo) REFERENCES public.emprestimos(id_emprestimo);


-- Completed on 2024-09-18 22:07:49

--
-- PostgreSQL database dump complete
--

