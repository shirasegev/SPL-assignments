class Activities:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, activity):
        self._conn.execute("""
            INSERT INTO Activities (product_id, quantity, activator_id, date) VALUES (?, ?, ?, ?)
        """, [activity.product_id, activity.quantity, activity.activator_id, activity.date])

    def find_all(self):
        c = self._conn.cursor()
        all = c.execute("""
            SELECT product_id, quantity, activator_id, date FROM Activities ORDER BY date ASC
        """).fetchall()
        return [Activity(*row) for row in all]

    def detailed_activity_report(self):
        c = self._conn.cursor()
        all = c.execute("""
                    SELECT Activities.date, Products.description , Activities.quantity, Employees.name, Suppliers.name FROM Activities
                    LEFT JOIN Products ON Activities.product_id = Products.id
                    LEFT JOIN Employees ON Activities.activator_id = Employees.id
                    LEFT JOIN Suppliers ON Activities.activator_id = Suppliers.id
                    ORDER BY Activities.date ASC
                """).fetchall()
        return all


class Activity:
    def __init__(self, product_id, quantity, activator_id, date):
        self.product_id = product_id
        self.quantity = quantity
        self.activator_id = activator_id
        self.date = date

    def __str__(self):
        return "(" + str(self.product_id) + ", " + str(self.quantity) + ", " + str(self.activator_id) + ", " + str(self.date) + ")"
