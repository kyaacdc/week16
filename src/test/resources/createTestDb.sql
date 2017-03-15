CREATE TABLE category
(
  id INTEGER PRIMARY KEY NOT NULL,
  description VARCHAR(255),
  name VARCHAR(255),
  category INTEGER,
  CONSTRAINT fkaxulebkcvnxjo9yegnhqnrc8j FOREIGN KEY (category) REFERENCES category (id)
);

CREATE TABLE productcard
(
  sku VARCHAR(255) PRIMARY KEY NOT NULL,
  amount INTEGER NOT NULL,
  dislikes INTEGER NOT NULL,
  likes INTEGER NOT NULL,
  name VARCHAR(255),
  price INTEGER NOT NULL,
  productDescription VARCHAR(255),
  category INTEGER,
  CONSTRAINT fkqb0uvey0cfqaojgi1ycpm6prj FOREIGN KEY (category) REFERENCES category (id)
);

CREATE TABLE visualization
(
  id INTEGER PRIMARY KEY NOT NULL,
  type INTEGER NOT NULL,
  url VARCHAR(255),
  productcard VARCHAR(255),
  CONSTRAINT fk8pqpd1igln97jjx2hem76flaw FOREIGN KEY (productcard) REFERENCES productcard (sku)
);

CREATE TABLE attributename
(
  name VARCHAR(255) PRIMARY KEY NOT NULL
);

CREATE TABLE attributevalue
(
  id INTEGER PRIMARY KEY NOT NULL,
  value VARCHAR(255),
  attributename VARCHAR(255) NOT NULL,
  productcard VARCHAR(255),
  CONSTRAINT fks5ol6732heyojpn8jdefcgrxf FOREIGN KEY (attributename) REFERENCES attributename (name),
  CONSTRAINT fksn9jqunt6xcro546pltuyt5m3 FOREIGN KEY (productcard) REFERENCES productcard (sku)
);

CREATE TABLE customer
(
  email VARCHAR(255) PRIMARY KEY NOT NULL,
  name VARCHAR(255),
  phone VARCHAR(255),
  subscribe BOOLEAN NOT NULL
);

CREATE TABLE ordermain
(
  orderid INTEGER PRIMARY KEY NOT NULL,
  address VARCHAR(255),
  status INTEGER NOT NULL,
  customer VARCHAR(255) NOT NULL,
  CONSTRAINT fkbot9y4e73sy98lf0gf8fedsp FOREIGN KEY (customer) REFERENCES customer (email)
);

CREATE TABLE orderitem
(
  id INTEGER PRIMARY KEY NOT NULL,
  amount INTEGER NOT NULL,
  totalprice INTEGER NOT NULL,
  ordermain INTEGER NOT NULL,
  productcard VARCHAR(255),
  CONSTRAINT fkndgjtdkw2gsds6jgw4s5u43eu FOREIGN KEY (ordermain) REFERENCES ordermain (orderid),
  CONSTRAINT fk4rhq663krpdkhcbwc0s0waud9 FOREIGN KEY (productcard) REFERENCES productcard (sku)
);

CREATE SEQUENCE public.attributevalue_id_seq NO MINVALUE NO MAXVALUE NO CYCLE;
ALTER TABLE public.attributevalue ALTER COLUMN id SET DEFAULT nextval('public.attributevalue_id_seq');
ALTER SEQUENCE public.attributevalue_id_seq OWNED BY public.attributevalue.id;

CREATE SEQUENCE public.category_id_seq NO MINVALUE NO MAXVALUE NO CYCLE;
ALTER TABLE public.category ALTER COLUMN id SET DEFAULT nextval('public.category_id_seq');
ALTER SEQUENCE public.category_id_seq OWNED BY public.category.id;

CREATE SEQUENCE public.orderitem_id_seq NO MINVALUE NO MAXVALUE NO CYCLE;
ALTER TABLE public.orderitem ALTER COLUMN id SET DEFAULT nextval('public.orderitem_id_seq');
ALTER SEQUENCE public.orderitem_id_seq OWNED BY public.orderitem.id;

CREATE SEQUENCE public.ordermain_orderid_seq NO MINVALUE NO MAXVALUE NO CYCLE;
ALTER TABLE public.ordermain ALTER COLUMN orderid SET DEFAULT nextval('public.ordermain_orderid_seq');
ALTER SEQUENCE public.ordermain_orderid_seq OWNED BY public.ordermain.orderid;

CREATE SEQUENCE public.visualization_id_seq NO MINVALUE NO MAXVALUE NO CYCLE;
ALTER TABLE public.visualization ALTER COLUMN id SET DEFAULT nextval('public.visualization_id_seq');
ALTER SEQUENCE public.visualization_id_seq OWNED BY public.visualization.id;