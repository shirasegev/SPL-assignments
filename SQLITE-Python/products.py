class Products:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, product):
        self._conn.execute("""
            INSERT INTO Products (id, description, price, quantity) VALUES (?, ?, ?, ?)
        """, [product.id, product.description, product.price, product.quantity])


    def find(self, id):
        c = self._conn.cursor()
        c.execute("""
                    SELECT id, description, price,quantity  FROM Products WHERE id = ?
                """, [id])
        return Product(*c.fetchone())

    def find_all(self):
        c = self._conn.cursor()
        all = c.execute("""
            SELECT id, description, price, quantity FROM Products ORDER BY id
        """).fetchall()
        return [Product(*row) for row in all]

    def update(self, product):
        self._conn.execute("""
                   UPDATE Products SET quantity=(?) WHERE id=(?)
               """, [product.quantity, product.id])

    def get_product_quantity(self, id):
        c = self._conn.cursor()
        c.execute("""SELECT quantity FROM Products WHERE id = ?""", [id])
        return c.fetchone


class Product:
    def __init__(self, id, description, price, quantity=0):
        self.id = id
        self.description = description
        self.price = price
        self.quantity = quantity

    def __str__(self):
        return "(" + str(self.id) + ", " + "'" + self.description + "'" + ", " + str(self.price) + ", " + str(self.quantity) + ")"
