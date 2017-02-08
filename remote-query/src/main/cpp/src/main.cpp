#include <infinispan/hotrod/BasicTypesProtoStreamMarshaller.h>
#include <infinispan/hotrod/ProtoStreamMarshaller.h>
#include "infinispan/hotrod/ConfigurationBuilder.h"
#include "infinispan/hotrod/RemoteCacheManager.h"
#include "infinispan/hotrod/RemoteCache.h"
#include "infinispan/hotrod/Query.h"
#include "infinispan/hotrod/QueryUtils.h"

#include "addressbook.pb.h"

#include <iostream>
#include <memory>

#define PROTOBUF_METADATA_CACHE_NAME "___protobuf_metadata"
#define ERRORS_KEY_SUFFIX  ".errors"

using namespace infinispan::hotrod;
using namespace ::google::protobuf;
using namespace ::org::infinispan::query::remote::client;

std::string read(std::string file)
{
    std::ifstream t(file);
    std::stringstream buffer;
    buffer << t.rdbuf();
    return buffer.str();
}

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


ostream& operator<<(ostream& out, const quickstart::Person& person) {
    out << person.ShortDebugString() << endl;
    return out;
}


static std::string APP_MENU = "\nAvailable actions:\n"
        "0. Display available actions\n"
        "1. Add person\n"
        "2. Remove person\n"
        "3. Add phone to person\n"
        "4. Remove phone from person\n"
        "5. Query persons by name\n"
        "6. Query persons by phone\n"
        "7. Add memo to person\n"
        "8. Full text search on memo\n"
        "9. Query for projection (name)\n"
        "10. Count person with mobile\n"
        "11. Display all cache entries\n"
        "12. Clear cache\n"
        "13. Quit\n";

void displayActions() {
    cout << APP_MENU << endl;
}

void putPerson(RemoteCache<int, quickstart::Person>& cache) {
    int id = read_valid<int>("Enter person id: ");
    string name = read_valid<string>("Enter person name: ");
    string email = read_valid<string>("Enter person email: ");

    quickstart::Person person;
    person.set_id(id);
    person.set_name(name);
    person.set_email(email);
    person.set_department((id%2==0) ?"EVEN" : "ODD");
    cache.put(id, person);
}

void removePerson(RemoteCache<int, quickstart::Person>& cache) {
    int id = read_valid<int>("Enter person id: ");
    cache.remove(id);
}


void addPhone(RemoteCache<int, quickstart::Person>& cache) {
    int id = read_valid<int>("Enter person id: ");

    quickstart::Person *person = cache.get(id);
    if (person == nullptr) {
        cerr << "Person not found." << endl;
        return;
    }

    cout << *person << endl;
    string number = read_valid<string>("Phone Number: ");
    string phone_type_str = read_valid<string>("Phone Type (MOBILE/HOME/WORK): ");

    quickstart::Person_PhoneType phone_type;
    quickstart::Person_PhoneType_Parse(phone_type_str, &phone_type);

    quickstart::Person_PhoneNumber *phoneNumber = person->add_phone();
    phoneNumber->set_number(number);
    phoneNumber->set_type(phone_type);

    cache.put(id, *person);
    delete person;

}

void removePhone(RemoteCache<int, quickstart::Person>& cache) {

    int id = read_valid<int>("Enter Person id: ");

    quickstart::Person* person= cache.get(id);

    if (person == nullptr) {
        cerr << "Person not found." << endl;
        return;
    }

    cout << *person << endl;

    int phone_index = read_valid<int>("Enter phone index: ");
    if ((phone_index < 0) || (phone_index >= person->phone_size())) {
        cerr << "Phone index '" << phone_index << "' is out of range." << endl;
        return;
    }

    person->mutable_phone()->DeleteSubrange(phone_index, 1);

    cache.put(id, *person);
    delete person;
}

void queryPersonByName(RemoteCache<int, quickstart::Person>& cache) {
    string name = read_valid<string>("Enter person name: ");
    QueryRequest qr;
    qr.set_querystring("from quickstart.Person where name='"+name+"'");
    QueryResponse resp = cache.query(qr);
    std::vector<quickstart::Person> res;
    if (!unwrapResults(resp, res)) {
        std::cerr << "error in creating the resultset" << std::endl;
    }
    cout << "Result set is:" << endl;
    for (auto i: res) {
        cout << i << endl;
    }
}

void queryPersonByPhone(RemoteCache<int, quickstart::Person>& cache) {
    string name = read_valid<string>("Enter phone number: ");
    QueryRequest qr;
    qr.set_querystring("from quickstart.Person as p where p.phone.number='"+name+"'");
    QueryResponse resp = cache.query(qr);
    std::vector<quickstart::Person> res;
    if (!unwrapResults(resp, res)) {
        std::cerr << "error in creating the resultset" << std::endl;
    }
    cout << "Result set is:" << endl;
    for (auto i: res) {
        cout << i << endl;
    }
}

void addMemo(RemoteCache<int, quickstart::Person>& cache) {
    int id = read_valid<int>("Enter person id: ");

    quickstart::Person *person = cache.get(id);
    if (person == nullptr) {
        cerr << "Person not found." << endl;
        return;
    }
    cout << *person << endl;

    cout << ("Memo text: ");
    string memotext;
    cin.ignore();
    getline(cin, memotext);
    quickstart::Person_Memo *memo = person->add_memo();
    memo->set_text(memotext);

    cache.put(id, *person);
    delete person;

}

void fullTextOnMemo(RemoteCache<int, quickstart::Person>& cache) {
    QueryRequest qr;
    qr.set_querystring("from quickstart.Person as p where p.memo.text : 'language'");
    QueryResponse resp = cache.query(qr);
    std::vector<quickstart::Person> res;
    if (!unwrapResults(resp, res)) {
        std::cerr << "error in creating the resultset" << std::endl;
    }
    cout << "Result set is:" << endl;
    for (auto i: res) {
        cout << i << endl;
    }
}

void printAll(RemoteCache<int, quickstart::Person>& cache) {
    QueryRequest qr;
    qr.set_querystring("from quickstart.Person");
    QueryResponse resp = cache.query(qr);
    std::vector<quickstart::Person> res;
    // unwrapProjection process the response and fill the given vector
    if (!unwrapResults(resp, res)) {
        std::cerr << "error in creating the resultset" << std::endl;
    }
    cout << "Result set is:" << endl;
    for (auto i: res) {
        cout << i << endl;
    }
}

void projectAllNames(RemoteCache<int, quickstart::Person>& cache) {
    QueryRequest qr;
    qr.set_querystring("select name from quickstart.Person");
    QueryResponse resp = cache.query(qr);
    std::vector<std::tuple<std::string>> nameVector;
    // unwrapProjection process the response if the result is not a set of entity/class
    // and fill the given vector with tuples of result.
    unwrapProjection(resp, nameVector);
    for (auto i: nameVector) {
        cout << get<0>(i) << endl;
    }
}

void countByDepartment(RemoteCache<int, quickstart::Person>& cache) {
    QueryRequest qr;
    qr.set_querystring("select count(p.id) from quickstart.Person p where p.department='ODD'");
    try
    {
        QueryResponse resp = cache.query(qr);
        int odd = unwrapSingleResult<int>(resp);
        qr.set_querystring("select count(p.id) from quickstart.Person p where p.department='EVEN'");
        resp = cache.query(qr);
        // unwrapSingleResult process the response if the result is a sigle row and column
        // the result is the function return value
        int even = unwrapSingleResult<int>(resp);
        cout << "Result is: " << odd << " odd and " << even << " even" << endl;
    }
    catch (Exception e) {
        cout << e.what() << endl;
    }
}



int readAction() {
    return read_valid<int>("> ");
}

int main(int argc, char *argv[]) {
  GOOGLE_PROTOBUF_VERIFY_VERSION;

  // Connect.
  ConfigurationBuilder builder;
  builder.addServer().host("127.0.0.1").port(11222);
  builder.protocolVersion(Configuration::PROTOCOL_VERSION_24);
  RemoteCacheManager cacheManager(builder.build());

  //server side setup:
  //    installing protobuf model for cache entries
  auto *km = new BasicTypesProtoStreamMarshaller<std::string>();
  auto *vm = new BasicTypesProtoStreamMarshaller<std::string>();

  RemoteCache<std::string, std::string> metadataCache = cacheManager.getCache<std::string, std::string>(
          km, &Marshaller<std::string>::destroy, vm, &Marshaller<std::string>::destroy,PROTOBUF_METADATA_CACHE_NAME, false);

  std::string s=read("/home/rigazilla/git/jboss-jdg-quickstarts/remote-query/src/main/cpp/src/addressbook.proto");
  metadataCache.put("q/a.proto"  , s);
  if (metadataCache.containsKey(ERRORS_KEY_SUFFIX))
  {
    std::cerr << "fail: error in registering .proto model" << std::endl;
    return -1;
  }

  auto *testkm = new BasicTypesProtoStreamMarshaller<int>();
  auto *testvm = new ProtoStreamMarshaller<quickstart::Person, quickstart::PersonType::id>();


  auto cache = cacheManager.getCache<int, quickstart::Person>(testkm, &Marshaller<int>::destroy, testvm, &Marshaller<quickstart::Person>::destroy, false);
  cacheManager.start();

  bool quit = false;
  displayActions();
  while (!quit) {
      int action = readAction();

      switch (action) {
      case 1:
          putPerson(cache);
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
          queryPersonByName(cache);
          break;
      case 6:
          queryPersonByPhone(cache);
          break;
      case 7:
          addMemo(cache);
          break;
      case 8:
          fullTextOnMemo(cache);
          break;
      case 9:
          projectAllNames(cache);
          break;
      case 10:
          countByDepartment(cache);
          break;
      case 11:
          printAll(cache);
          break;
      case 12:
          cache.clear();
          cout << "Cache cleared" << endl;
          break;
      case 13:
          quit = true;
          cout << "Bye!" << endl;
          break;
      default:
          std::cerr << "Invalid action: " << action << std::endl;
      case 0:
          displayActions();
      }
  }
}
