from repository import repo


def print_activities():
	print('Activities')
	for activity in repo.activities.find_all():
		print(activity)


def print_coffee_stands():
	print('Coffee stands')
	for coffee_stand in repo.coffee_stands.find_all():
		print(coffee_stand)


def print_employees():
	print('Employees')
	for employee in repo.employees.find_all():
		print(employee)


def print_products():
	print('Products')
	for product in repo.products.find_all():
		print(product)


def print_suppliers():
	print('Suppliers')
	for supplier in repo.suppliers.find_all():
		print(supplier)


def print_employees_report():
	print('')
	print('Employees report')
	activities = repo.activities.find_all()
	for employee in sorted(repo.employees.find_all(), key=lambda emp: emp.name):
		gain = 0
		for activity in activities:
			if activity.activator_id == employee.id:
				product = repo.products.find(activity.product_id)
				gain += product.price * abs(activity.quantity)
		coffee_stand = repo.coffee_stands.find(employee.coffee_stand)
		print(employee.name, str(employee.salary), coffee_stand.location, gain)


def print_detailed_activity_report():
	report = repo.activities.detailed_activity_report()
	if len(report) > 0:
		print('')
		print('Activities')
		for activity in report:
			print(activity)


def print_db():
	print_activities()
	print_coffee_stands()
	print_employees()
	print_products()
	print_suppliers()
	print_employees_report()
	print_detailed_activity_report()


if __name__ == '__main__':
	print_db()
