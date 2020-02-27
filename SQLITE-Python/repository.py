import sqlite3
import atexit
from employees import Employees
from suppliers import Suppliers
from products import Products
from coffee_stands import CoffeeStands
from activities import Activities


# The _Repository
class Repository(object):
    def __init__(self):
        self._conn = sqlite3.connect('moncafe.db')
        self.employees = Employees(self._conn)
        self.suppliers = Suppliers(self._conn)
        self.products = Products(self._conn)
        self.coffee_stands = CoffeeStands(self._conn)
        self.activities = Activities(self._conn)

    def _close(self):
        self._conn.commit()
        self._conn.close()

    def create_tables(self):
        self._conn.executescript("""
        CREATE TABLE Employees(
            id          INTEGER     PRIMARY KEY,
            name        TEXT        NOT NULL,
            salary      REAL        NOT NULL,
            coffee_stand INTEGER REFERENCES Coffee_stand (id)
        );
        CREATE TABLE Suppliers(
            id      INTEGER     PRIMARY KEY,
            name    TEXT        NOT NULL,
            contact_information TEXT
        );
        CREATE TABLE Products(
            id              INTEGER     PRIMARY KEY,
            description     TEXT        NOT NULL,
            price           REAL        NOT NULL,
            quantity        INTEGER     NOT NULL
        );
        CREATE TABLE Coffee_stands(
            id                      INTEGER     PRIMARY KEY,
            location                TEXT        NOT NULL,
            number_of_employees     INTEGER
        );
        CREATE TABLE Activities(
            product_id      INTEGER     REFERENCES Product(id),
            quantity        INTEGER     NOT NULL,
            activator_id    INTEGER     NOT NULL,
            date            DATE        NOT NULL
        );
        """)


# the repository singleton
repo = Repository()
atexit.register(repo._close)
