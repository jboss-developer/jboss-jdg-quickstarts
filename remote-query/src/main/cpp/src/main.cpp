#include "infinispan/hotrod/ConfigurationBuilder.h"
#include "infinispan/hotrod/RemoteCacheManager.h"
#include "infinispan/hotrod/RemoteCache.h"

#include "addressbook.pb.h"

#include "Marshalling.h"

#include <iostream>

using namespace quickstart::primitive;
using namespace quickstart::marshalling;
using namespace infinispan::hotrod;
using namespace std;
using namespace std::tr1;
using namespace ::google::protobuf;

template<class T> T read_valid(string prompt) {
    T result;
    while (true) {
        cout << prompt;
        if (!(cin >> result)) {
            string str;
            cin.clear();
            cin >> str;
            cerr << "Invalid input \"" << str << "\". Try again." << endl;
        } else {
            return result;
        }
    }
}

ostream& operator<<(ostream& out, const tutorial::Person& person) {
    out << person.ShortDebugString() << endl;
    return out;
}

void displayActions() {
    cout << "Available actions:" << endl
         << "0. Display available actions" << endl
         << "1. Add person" << endl
         << "2. Remove person" << endl
         << "3. Add phone" << endl
         << "4. Remove phone" << endl
         << "5. Print all" << endl
         << "6. Quit" << endl << endl;
}

void addPerson(RemoteCache<string, string>& cache) {
    int id = read_valid<int>("Enter person id: ");
    string name = read_valid<string>("Enter person name: ");
    string email = read_valid<string>("Enter person email: ");

    tutorial::Person person;
    person.set_id(id);
    person.set_name(name);
    person.set_email(email);
    
    cache.put(*marshal(id), *marshal(person));
}

void removePerson(RemoteCache<string, string>& cache) {
    int id = read_valid<int>("Enter person id: ");
    cache.remove(*marshal(id));
}

void addPhone(RemoteCache<string, string>& cache) {
    int id = read_valid<int>("Enter person id: ");

    string key = *marshal(id);

    string *value = cache.get(key);
    if (value == NULL) {
        cerr << "Person not found." << endl;
        return;
    }

    tutorial::Person person = *unmarshal<tutorial::Person>(*value);
    cout << person << endl;

    string number = read_valid<string>("Phone Number: ");
    string phone_type_str = read_valid<string>("Phone Type (MOBILE/HOME/WORK): ");

    tutorial::Person_PhoneType phone_type;
    tutorial::Person_PhoneType_Parse(phone_type_str, &phone_type);

    tutorial::Person_PhoneNumber *phoneNumber = person.add_phone();
    phoneNumber->set_number(number);
    phoneNumber->set_type(phone_type);

    cache.put(key, *marshal(person));
}

void removePhone(RemoteCache<string, string>& cache) {
    int id = read_valid<int>("Enter person id: ");

    string key = *marshal(id);

    string *value = cache.get(key);
    if (value == NULL) {
        cerr << "Person not found." << endl;
        return;
    }

    tutorial::Person person = *unmarshal<tutorial::Person>(*value);
    cout << person << endl;

    int phone_index = read_valid<int>("Enter phone index: ");
    if ((phone_index < 0) || (phone_index >= person.phone_size())) {
        cerr << "Phone index '" << phone_index << "' is out of range." << endl;
        return;
    }

    person.mutable_phone()->DeleteSubrange(phone_index, 1);

    cache.put(key, *marshal(person));
}

void printAll(RemoteCache<string, string>& cache) {
    set<shared_ptr<string> > key_set = cache.keySet();
    for (set<shared_ptr<string> >::const_iterator i = key_set.begin(); i != key_set.end(); ++i) {
        tutorial::Person person = *unmarshal<tutorial::Person>(*cache.get(**i));
        cout << person << endl;
    }
}

int readAction() {
    return read_valid<int>("> ");
}

int main(int argc, char *argv[]) {
  GOOGLE_PROTOBUF_VERIFY_VERSION;

  // Connect.
  ConfigurationBuilder builder;
  builder.addServer().host(argc > 1 ? argv[1] : "127.0.0.1").port(argc > 2 ? atoi(argv[2]) : 11222);

  RemoteCacheManager cacheManager(builder.build());
  RemoteCache<string, string> cache = cacheManager.getCache<string, string>("addressbook", false);
  cacheManager.start();

  bool quit = false;
  displayActions();
  while (!quit) {
      int action = readAction();

      switch (action) {
      case 1:
          addPerson(cache);
          break;
      case 2:
          removePerson(cache);
          break;
      case 3:
          addPhone(cache);
          break;
      case 4:
          removePhone(cache);
          break;
      case 5:
          printAll(cache);
          break;
      case 6:
          quit = true;
          break;
      default:
          cerr << "Invalid action: " << action << endl;
      case 0:
          displayActions();
      }
  }

  // // Create some protobuf messages.
  // tutorial::AddressBook address_book;

  // tutorial::Person *p0 = address_book.add_person();
  // tutorial::Person *p1 = address_book.add_person();

  // p0->set_id(0);
  // p0->set_name("person0");

  // p1->set_id(1);
  // p1->set_name("person1");

  // // Marshal Keys.
  // shared_ptr<string> key1 = marshal("key1");
  // shared_ptr<string> key2 = marshal("key2");
  // shared_ptr<string> key3 = marshal("key3");

  // // Marshal Values.
  // shared_ptr<string> value1 = marshal(address_book);
  // shared_ptr<string> value2 = marshal(string("value2"));
  // shared_ptr<string> value3 = marshal(3.0);

  // // Write to cache.
  // cache.put(*key1, *value1);
  // cache.put(*key2, *value2);
  // cache.put(*key3, *value3);

  // // Fetch from cache.
  // auto_ptr<string> marshalled_value_fetched1(cache.get(*key1));
  // auto_ptr<string> marshalled_value_fetched2(cache.get(*key2));
  // auto_ptr<string> marshalled_value_fetched3(cache.get(*key3));

  // // Don't know what the type of the values should be.
  // {
  //   // Register marshallers for the known types.
  //   MarshallingRegistry registry;
  //   registry.registerMarshaller("tutorial.AddressBook",
  //                               shared_ptr<quickstart::marshalling::Marshaller>(new DefaultMarshaller<tutorial::AddressBook>));

  //   shared_ptr<Message> message_fetched1 = registry.unmarshal(*marshalled_value_fetched1);
  //   shared_ptr<Message> message_fetched2 = registry.unmarshal(*marshalled_value_fetched2);
  //   shared_ptr<Message> message_fetched3 = registry.unmarshal(*marshalled_value_fetched3);

  //   if ("tutorial.AddressBook" == message_fetched1->GetTypeName()) {
  //     tutorial::AddressBook value_fetched1 = *((tutorial::AddressBook*) message_fetched1.get());
  //     std::cout << "value_fetched1: " << value_fetched1.GetTypeName() << endl;
  //   }

  //   if ("quickstart.primitive.String" == message_fetched2->GetTypeName()) {
  //     string value_fetched2 = ((String*) message_fetched2.get())->value();
  //     std::cout << "value_fetched2: " << value_fetched2 << endl;
  //   }

  //   if ("quickstart.primitive.Double" == message_fetched3->GetTypeName()) {
  //     double value_fetched3 = ((Double*) message_fetched3.get())->value();
  //     std::cout << "value_fetched3: " << value_fetched3 << endl;
  //   }
  // }

  // // I know what the type of the values should be.
  // {
  //   tutorial::AddressBook value_fetched1 = *unmarshal<tutorial::AddressBook>(*marshalled_value_fetched1);
  //   std::cout << "known type value_fetched1: " << value_fetched1.GetTypeName() << endl;

  //   string value_fetched2 = unmarshalString(*marshalled_value_fetched2);
  //   std::cout << "known type value_fetched2: " << value_fetched2 << endl;

  //   double value_fetched3 = unmarshalDouble(*marshalled_value_fetched3);
  //   std::cout << "known type value_fetched3: " << value_fetched3 << endl;
  // }

  // Clean-up.
  cacheManager.stop();
  
  google::protobuf::ShutdownProtobufLibrary();
}
