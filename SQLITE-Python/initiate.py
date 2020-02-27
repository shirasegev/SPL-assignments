import os
import sys
from employees import Employee
from suppliers import Supplier
from products import Product
from coffee_stands import CoffeeStand

if os.path.exists('moncafe.db'):
    os.remove('moncafe.db')

from repository import repo

repo.create_tables()

config_file = open(sys.argv[1], "r")
config_file_string = config_file.read()

for line in config_file_string.split('\n'):
    args = line.split(', ')

    if args[0] == 'E':
        repo.employees.insert(Employee(args[1], args[2], args[3], args[4]))

    elif args[0] == 'S':
        repo.suppliers.insert(Supplier(args[1], args[2], args[3].strip())) # remove \n from end of line

    elif args[0] == 'P':
        repo.products.insert(Product(args[1], args[2], args[3]))

    elif args[0] == 'C':
        repo.coffee_stands.insert(CoffeeStand(args[1], args[2], args[3]))
