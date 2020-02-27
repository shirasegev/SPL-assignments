class Suppliers:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, supplier):
        self._conn.execute("""
                INSERT INTO Suppliers (id, name, contact_information) VALUES (?, ?, ?)
        """, [supplier.id, supplier.name, supplier.contact_information])

    def find_all(self):
        c = self._conn.cursor()
        all = c.execute("""
            SELECT id, name, contact_information FROM Suppliers ORDER BY id
        """).fetchall()
        return [Supplier(*row) for row in all]


class Supplier:
    def __init__(self, id, name, contact_information):
        self.id = id
        self.name = name
        self.contact_information = contact_information

    def __str__(self):
        return "(" + str(self.id) + ", '" + self.name + "', '" + self.contact_information + "')"
