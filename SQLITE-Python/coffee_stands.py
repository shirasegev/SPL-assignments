class CoffeeStands:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, coffee_stand):
        self._conn.execute("""
            INSERT INTO Coffee_stands (id, location, number_of_employees) VALUES (?, ?, ?)
        """, [coffee_stand.id, coffee_stand.location, coffee_stand.number_of_employees])

    def find_all(self):
        c = self._conn.cursor()
        all = c.execute("""
            SELECT id, location, number_of_employees FROM Coffee_stands ORDER BY id
        """).fetchall()
        return [CoffeeStand(*row) for row in all]

    def find(self, id):
        c = self._conn.cursor()
        c.execute("""
                    SELECT id, location, number_of_employees FROM Coffee_stands WHERE id = ?
                """, [id])
        return CoffeeStand(*c.fetchone())


class CoffeeStand:
    def __init__(self, id, location, number_of_employees):
        self.id = id
        self.location = location
        self.number_of_employees = number_of_employees

    def __str__(self):
        return "(" + str(self.id) + ", " + "'" + self.location + "'" + ", " + str(self.number_of_employees) + ")"