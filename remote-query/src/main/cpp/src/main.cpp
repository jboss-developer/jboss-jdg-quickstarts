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

int main(int argc, char *argv[])

{
  GOOGLE_PROTOBUF_VERIFY_VERSION;

  // Connect.
  ConfigurationBuilder builder;
  builder.addServer().host(argc > 1 ? argv[1] : "127.0.0.1").port(argc > 2 ? atoi(argv[2]) : 11222);

  RemoteCacheManager cacheManager(builder.build(), false);
  RemoteCache<string, string> cache = cacheManager.getCache<string, string>();
  cacheManager.start();

  // Create some protobuf messages.
  tutorial::AddressBook address_book;

  tutorial::Person *p0 = address_book.add_person();
  tutorial::Person *p1 = address_book.add_person();

  p0->set_id(0);
  p0->set_name("person0");

  p1->set_id(1);
  p1->set_name("person1");

  // Marshal Keys.
  shared_ptr<string> key1 = marshal("key1");
  shared_ptr<string> key2 = marshal("key2");
  shared_ptr<string> key3 = marshal("key3");

  // Marshal Values.
  shared_ptr<string> value1 = marshal(address_book);
  shared_ptr<string> value2 = marshal(string("value2"));
  shared_ptr<string> value3 = marshal(3.0);

  // Write to cache.
  cache.put(*key1, *value1);
  cache.put(*key2, *value2);
  cache.put(*key3, *value3);

  // Fetch from cache.
  auto_ptr<string> marshalled_value_fetched1(cache.get(*key1));
  auto_ptr<string> marshalled_value_fetched2(cache.get(*key2));
  auto_ptr<string> marshalled_value_fetched3(cache.get(*key3));

  // Don't know what the type of the values should be.
  {
    // Register marshallers for the known types.
    MarshallingRegistry registry;
    registry.registerMarshaller("tutorial.AddressBook",
                                shared_ptr<quickstart::marshalling::Marshaller>(new DefaultMarshaller<tutorial::AddressBook>));

    shared_ptr<Message> message_fetched1 = registry.unmarshal(*marshalled_value_fetched1);
    shared_ptr<Message> message_fetched2 = registry.unmarshal(*marshalled_value_fetched2);
    shared_ptr<Message> message_fetched3 = registry.unmarshal(*marshalled_value_fetched3);

    if ("tutorial.AddressBook" == message_fetched1->GetTypeName()) {
      tutorial::AddressBook value_fetched1 = *((tutorial::AddressBook*) message_fetched1.get());
      std::cout << "value_fetched1: " << value_fetched1.GetTypeName() << endl;
    }

    if ("quickstart.primitive.String" == message_fetched2->GetTypeName()) {
      string value_fetched2 = ((String*) message_fetched2.get())->value();
      std::cout << "value_fetched2: " << value_fetched2 << endl;
    }

    if ("quickstart.primitive.Double" == message_fetched3->GetTypeName()) {
      double value_fetched3 = ((Double*) message_fetched3.get())->value();
      std::cout << "value_fetched3: " << value_fetched3 << endl;
    }
  }

  // I know what the type of the values should be.
  {
    tutorial::AddressBook value_fetched1 = *unmarshal<tutorial::AddressBook>(*marshalled_value_fetched1);
    std::cout << "known type value_fetched1: " << value_fetched1.GetTypeName() << endl;

    string value_fetched2 = unmarshalString(*marshalled_value_fetched2);
    std::cout << "known type value_fetched2: " << value_fetched2 << endl;

    double value_fetched3 = unmarshalDouble(*marshalled_value_fetched3);
    std::cout << "known type value_fetched3: " << value_fetched3 << endl;
  }

  // Clean-up.
  cacheManager.stop();
  
  google::protobuf::ShutdownProtobufLibrary();
}
