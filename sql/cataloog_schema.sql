CREATE SEQUENCE calcIdSeq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE Calculations (
    CalcId INTEGER DEFAULT nextval('calcIdSeq') NOT NULL,
    Expression TEXT NOT NULL,
    Result VARCHAR(255) NOT NULL,
    Success BOOL NOT NULL
);