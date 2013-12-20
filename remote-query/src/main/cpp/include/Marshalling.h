#ifndef QUICKSTART_WRAPPING_H
#define QUICKSTART_WRAPPING_H

#include "message-wrapping.pb.h"
#include "primitive.pb.h"

#include <google/protobuf/message.h>
#include <map>
#include <sstream>
#include <string>
#include <tr1/memory>

using namespace ::google::protobuf;
using namespace std;
using namespace std::tr1;
using namespace org::infinispan::protostream;
using namespace quickstart::primitive;

namespace quickstart {
  namespace marshalling {

    shared_ptr<string> marshal(const double& val);
    shared_ptr<string> marshal(const float& val);
    shared_ptr<string> marshal(const int32& val);
    shared_ptr<string> marshal(const uint32& val);
    shared_ptr<string> marshal(const int64& val);
    shared_ptr<string> marshal(const uint64& val);
    shared_ptr<string> marshal(const bool& val);
    shared_ptr<string> marshal(const string& val);
    shared_ptr<string> marshal(const Message& message);
    shared_ptr<string> marshal(const char* val);

    double unmarshalDouble(const string& str);
    float unmarshalFloat(const string& str);

    int32 unmarshalInt32(const string& str);
    int32 unmarshalSFixed32(const string& str);
    int32 unmarshalSInt32(const string& str);
    uint32 unmarshalUInt32(const string& str);
    uint32 unmarshalFixed32(const string& str);

    int64 unmarshalInt64(const string& str);
    int64 unmarshalSFixed64(const string& str);
    int64 unmarshalSInt64(const string& str);
    uint64 unmarshalUInt64(const string& str);
    uint64 unmarshalFixed64(const string& str);

    bool unmarshalBool(const string& str);
    string unmarshalString(const string& str);
    string unmarshalBytes(const string& str);

    void fromProtoSerialization(const string& str, Message& message);

    class MarshallingException : public std::exception {
    public:
      MarshallingException(std::string msg) : msg(msg) {
      }

      ~MarshallingException() throw() {
      }

      virtual const char* what() const throw() {
        return msg.c_str();
      }
    private:
      std::string msg;
    };

    template <class T> shared_ptr<T> unmarshal(const string& str) {
      Message *result = new T;

      WrappedMessage wm;
      fromProtoSerialization(str, wm);

      if (!wm.has_wrappeddescriptorfullname()) {
        std::string message("Not a proto object: ");
        message.append(str);
        throw MarshallingException(message);
      }
      fromProtoSerialization(wm.wrappedmessagebytes(), *result);
  
      return shared_ptr<T>((T*) result);
    }

    class Marshaller {
    public:
      virtual shared_ptr<Message> unmarshal(const string& str) = 0;
    };

    template <class T> class DefaultMarshaller : public Marshaller {
    public:
      shared_ptr<Message> unmarshal(const string& str) {
        return quickstart::marshalling::unmarshal<T>(str);
      }
    };

    class MarshallingRegistry {
    public:
      void registerMarshaller(const string& fullname, shared_ptr<Marshaller> marshaller);
      shared_ptr<Message> unmarshal(const string& str);
      
    private:
      map<string, shared_ptr<Marshaller> > marshaller_by_name;
    };

  }
}
#endif
