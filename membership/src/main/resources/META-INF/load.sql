-- 0 = FREE, 1 = FULL
insert into PERSON (ID, SURNAME, EMAIL) values (1, 'Kruger', 'phillip.kruger@gmail.com')
insert into MEMBERSHIP (ID, OWNER_ID, TYPE) values (1, 1, 1)
insert into PERSON_NAMES (PERSON_ID, NAME) values (1, 'Natus')
insert into PERSON_NAMES (PERSON_ID, NAME) values (1, 'Phillip')

insert into PERSON (ID, SURNAME, EMAIL) values (2, 'Kruger', 'charmaine.kruger@gmail.com')
insert into MEMBERSHIP (ID, OWNER_ID, TYPE) values (2, 2, 1)
insert into PERSON_NAMES (PERSON_ID, NAME) values (2, 'Charmaine')
insert into PERSON_NAMES (PERSON_ID, NAME) values (2, 'Juliet')

insert into PERSON (ID, SURNAME, EMAIL) values (3, 'van der Merwe', 'koos@gmail.com')
insert into MEMBERSHIP (ID, OWNER_ID, TYPE) values (3, 3, 1)
insert into PERSON_NAMES (PERSON_ID, NAME) values (3, 'Koos')

insert into PERSON (ID, SURNAME, EMAIL) values (4, 'van der Westhuizen', 'minki@gmail.com')
insert into MEMBERSHIP (ID, OWNER_ID, TYPE) values (4, 4, 0)
insert into PERSON_NAMES (PERSON_ID, NAME) values (4, 'Minki')