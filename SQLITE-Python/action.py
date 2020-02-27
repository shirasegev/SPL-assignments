import sys
from repository import repo
from activities import Activity
from printdb import print_db


activities_file = open(sys.argv[1], "r")

activities_file_string = activities_file.read()


for line in activities_file_string.strip().split('\n'):
    action = line[:].split(', ')
    product_id = int(action[0])
    action_quantity = int(action[1])
    activator_id = int(action[2])
    date = action[3].strip()

    product = repo.products.find(product_id)

    if (action_quantity >= 0) or (action_quantity < 0 and product.quantity >= abs(action_quantity)):
        product.quantity += action_quantity
        repo.products.update(product)
        repo.activities.insert(Activity(product_id, action_quantity, activator_id, date))

print_db()
