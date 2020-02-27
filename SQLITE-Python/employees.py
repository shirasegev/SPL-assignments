class Employees:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, employee):
        self._conn.execute("""
               INSERT INTO Employees (id, name, salary, coffee_stand) VALUES (?, ?, ?, ?)
           """, [employee.id, employee.name, employee.salary, employee.coffee_stand])

    def find_all(self):
        c = self._conn.cursor()
        all = c.execute("""
            SELECT id, name, salary, coffee_stand FROM Employees ORDER BY id
        """).fetchall()
        return [Employee(*row) for row in all]


class Employee:
    def __init__(self, id, name, salary, coffee_stand):
        self.id = id
        self.name = name
        self.salary = salary
        self.coffee_stand = coffee_stand

    def __str__(self):
        return "(" + str(self.id) + ", '" + self.name + "', " + str(self.salary) + ", " + str(self.coffee_stand) + ")"
