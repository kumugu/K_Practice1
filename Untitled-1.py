class Contact:
    def __init__(self, name, phone, email):
        self.name = name
        self.phone = phone
        self.email = email

    def __str__(self):
        return f"Name: {self.name}, Phone: {self.phone}, Email: {self.email}"


class AddressBook:
    def __init__(self):
        self.contacts = []

    def add_contact(self, contact):
        self.contacts.append(contact)
        print(f"Contact {contact.name} added successfully.")

    def remove_contact(self, name):
        contact = self.find_contact(name)
        if contact:
            self.contacts.remove(contact)
            print(f"Contact {name} removed successfully.")
        else:
            print(f"Contact {name} not found.")

    def update_contact(self, name, phone=None, email=None):
        contact = self.find_contact(name)
        if contact:
            if phone:
                contact.phone = phone
            if email:
                contact.email = email
            print(f"Contact {name} updated successfully.")
        else:
            print(f"Contact {name} not found.")

    def find_contact(self, name):
        for contact in self.contacts:
            if contact.name == name:
                return contact
        return None

    def list_contacts(self):
        if not self.contacts:
            print("Address book is empty.")
        else:
            for contact in self.contacts:
                print(contact)


def main():
    address_book = AddressBook()

    while True:
        print("\nAddress Book Menu:")
        print("1. Add Contact")
        print("2. Remove Contact")
        print("3. Update Contact")
        print("4. Find Contact")
        print("5. List Contacts")
        print("6. Exit")

        choice = input("Choose an option: ")

        if choice == '1':
            name = input("Enter name: ")
            phone = input("Enter phone: ")
            email = input("Enter email: ")
            contact = Contact(name, phone, email)
            address_book.add_contact(contact)

        elif choice == '2':
            name = input("Enter name of the contact to remove: ")
            address_book.remove_contact(name)

        elif choice == '3':
            name = input("Enter name of the contact to update: ")
            phone = input("Enter new phone (leave empty to skip): ")
            email = input("Enter new email (leave empty to skip): ")
            phone = phone if phone else None
            email = email if email else None
            address_book.update_contact(name, phone, email)

        elif choice == '4':
            name = input("Enter name of the contact to find: ")
            contact = address_book.find_contact(name)
            if contact:
                print(contact)
            else:
                print(f"Contact {name} not found.")

        elif choice == '5':
            address_book.list_contacts()

        elif choice == '6':
            print("Exiting the address book. Goodbye!")
            break

        else:
            print("Invalid choice, please choose again.")


if __name__ == "__main__":
    main()
