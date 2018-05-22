-- 0 = FREE, 1 = FULL
insert into PERSON (ID, SURNAME) values (1, 'Kruger')
insert into MEMBERSHIP (ID, OWNER_ID, TYPE) values (1, 1, 1)
insert into PERSON_NAMES (PERSON_ID, NAME) values (1, 'Natus')
insert into PERSON_NAMES (PERSON_ID, NAME) values (1, 'Phillip')

insert into PERSON (ID, SURNAME) values (2, 'Kruger')
insert into MEMBERSHIP (ID, OWNER_ID, TYPE) values (2, 2, 1)
insert into PERSON_NAMES (PERSON_ID, NAME) values (2, 'Charmaine')
insert into PERSON_NAMES (PERSON_ID, NAME) values (2, 'Juliet')

insert into PERSON (ID, SURNAME) values (3, 'van der Merwe')
insert into MEMBERSHIP (ID, OWNER_ID, TYPE) values (3, 3, 1)
insert into PERSON_NAMES (PERSON_ID, NAME) values (3, 'Koos')

insert into PERSON (ID, SURNAME) values (4, 'van der Westhuizen')
insert into MEMBERSHIP (ID, OWNER_ID, TYPE) values (4, 4, 0)
insert into PERSON_NAMES (PERSON_ID, NAME) values (4, 'Minki')