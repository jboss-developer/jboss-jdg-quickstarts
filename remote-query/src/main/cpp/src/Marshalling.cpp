#include "Marshalling.h"

namespace quickstart {
  namespace marshalling {

    shared_ptr<Message> unmarshalPrimitive(const WrappedMessage& wm) {
      Message *result;
      if (wm.has_wrappeddouble()) {
        result = new Double;
        ((Double*) result)->set_value(wm.wrappeddouble());
      } else if (wm.has_wrappedfloat()) {
        result = new Float;
        ((Float*) result)->set_value(wm.wrappedfloat());
      } else if (wm.has_wrappedint32()) {
        result = new Int32;
        ((Int32*) result)->set_value(wm.wrappedint32());
      } else if (wm.has_wrappedsfixed32()) {
        result = new SFixed32;
        ((SFixed32*) result)->set_value(wm.wrappedsfixed32());
      } else if (wm.has_wrappedsint32()) {
        result = new SInt32;
        ((SInt32*) result)->set_value(wm.wrappedsint32());
      } else if (wm.has_wrappeduint32()) {
        result = new UInt32;
        ((UInt32*) result)->set_value(wm.wrappeduint32());
      } else if (wm.has_wrappedfixed32()) {
        result = new Fixed32;
        ((Fixed32*) result)->set_value(wm.wrappedfixed32());
      } else if (wm.has_wrappedint64()) {
        result = new Int64;
        ((Int64*) result)->set_value(wm.wrappedint64());
      } else if (wm.has_wrappedsfixed64()) {
        result = new SFixed64;
        ((SFixed64*) result)->set_value(wm.wrappedsfixed64());
      } else if (wm.has_wrappedsint64()) {
        result = new SInt64;
        ((SInt64*) result)->set_value(wm.wrappedsint64());
      } else if (wm.has_wrappeduint64()) {
        result = new UInt64;
        ((UInt64*) result)->set_value(wm.wrappeduint64());
      } else if (wm.has_wrappedfixed64()) {
        result = new Fixed64;
        ((Fixed64*) result)->set_value(wm.wrappedfixed64());
      } else if (wm.has_wrappedbool()) {
        result = new Bool;
        ((Bool*) result)->set_value(wm.wrappedbool());
      } else if (wm.has_wrappedstring()) {
        result = new String;
        ((String*) result)->set_value(wm.wrappedstring());
      } else if (wm.has_wrappedbytes()) {
        result = new Bytes;
        ((Bytes*) result)->set_value(wm.wrappedbytes());
      } else {
        throw MarshallingException("Unknown primitive!");
      }

      return shared_ptr<Message>(result);
    }
 
    shared_ptr<string> toProtoSerialization(const Message& message)
    {
      ostringstream output;
      if (!message.SerializeToOstream(&output)) {
        throw MarshallingException("ProtoBuf serialization failed!");
      }
      return shared_ptr<string>(new string(output.str()));
    }

    void fromProtoSerialization(const string& str, Message& message)
    {
      if (!message.ParseFromString(str)) {
        throw MarshallingException("ProtoBud deserialization failed!");
      }
    }

    void MarshallingRegistry::registerMarshaller(const string& fullname, shared_ptr<Marshaller> marshaller) {
      marshaller_by_name[fullname] = marshaller;
    }

    shared_ptr<Message> MarshallingRegistry::unmarshal(const string& str) {
      WrappedMessage wm;
      fromProtoSerialization(str, wm);

      if (!wm.has_wrappeddescriptorfullname()) {
        return unmarshalPrimitive(wm);
      }

      shared_ptr<Marshaller> marshaller = marshaller_by_name.at(wm.wrappeddescriptorfullname());
      return marshaller->unmarshal(str);
    }

    shared_ptr<string> marshal(const double& val)
    {
      WrappedMessage wm;
      wm.set_wrappeddouble(val);
      return toProtoSerialization(wm);
    }

    shared_ptr<string> marshal(const float& val) {
      WrappedMessage wm;
      wm.set_wrappedfloat(val);
      return toProtoSerialization(wm);
    }

    shared_ptr<string> marshal(const int32& val) {
      WrappedMessage wm;
      wm.set_wrappedint32(val);
      return toProtoSerialization(wm);
    }

    shared_ptr<string> marshal(const uint32& val) {
      WrappedMessage wm;
      wm.set_wrappeduint32(val);
      return toProtoSerialization(wm);
    }

    shared_ptr<string> marshal(const int64& val) {
      WrappedMessage wm;
      wm.set_wrappedint64(val);
      return toProtoSerialization(wm);
    }

    shared_ptr<string> marshal(const uint64& val) {
      WrappedMessage wm;
      wm.set_wrappeduint64(val);
      return toProtoSerialization(wm);
    }

    shared_ptr<string> marshal(const bool& val) {
      WrappedMessage wm;
      wm.set_wrappedbool(val);
      return toProtoSerialization(wm);
    }

    shared_ptr<string> marshal(const string& val) {
      WrappedMessage wm;
      wm.set_wrappedstring(val);
      return toProtoSerialization(wm);
    }

    shared_ptr<string> marshal(const char* val) {
      return marshal(string(val));
    }

    shared_ptr<string> marshal(const Message& message) {
      WrappedMessage wm;
      wm.set_wrappeddescriptorfullname(message.GetTypeName());
      wm.set_wrappedmessagebytes(*toProtoSerialization(message));
      return toProtoSerialization(wm);
    }

    double unmarshalDouble(const string& str) {
      WrappedMessage wm;
      fromProtoSerialization(str, wm);

      if (!wm.has_wrappeddouble()) {
        throw MarshallingException("Not double!");
      }

      return wm.wrappeddouble();
    }

    float unmarshalFloat(const string& str) {
      WrappedMessage wm;
      fromProtoSerialization(str, wm);

      if (!wm.has_wrappeddouble()) {
        throw MarshallingException("Not double!");
      }

      return wm.wrappeddouble();
    }

    int32 unmarshalInt32(const string& str) {
      WrappedMessage wm;
      fromProtoSerialization(str, wm);

      if (!wm.has_wrappedint32()) {
        throw MarshallingException("Not int32!");
      }

      return wm.wrappedint32();
    }

    int32 unmarshalSFixed32(const string& str) {
      WrappedMessage wm;
      fromProtoSerialization(str, wm);

      if (!wm.has_wrappedsfixed32()) {
        throw MarshallingException("Not sfixed32!");
      }

      return wm.wrappedint32();
    }

    int32 unmarshalSInt32(const string& str) {
      WrappedMessage wm;
      fromProtoSerialization(str, wm);

      if (!wm.has_wrappedsint32()) {
        throw MarshallingException("Not sint32!");
      }

      return wm.wrappedint32();
    }

    uint32 unmarshalUInt32(const string& str) {
      WrappedMessage wm;
      fromProtoSerialization(str, wm);

      if (!wm.has_wrappeduint32()) {
        throw MarshallingException("Not uint32!");
      }

      return wm.wrappeduint32();
    }

    uint32 unmarshalFixed32(const string& str) {
      WrappedMessage wm;
      fromProtoSerialization(str, wm);

      if (!wm.has_wrappedfixed32()) {
        throw MarshallingException("Not fixed32!");
      }

      return wm.wrappeduint32();
    }

    int64 unmarshalInt64(const string& str) {
      WrappedMessage wm;
      fromProtoSerialization(str, wm);

      if (!wm.has_wrappedint64()) {
        throw MarshallingException("Not int64!");
      }

      return wm.wrappedint64();
    }

    int64 unmarshalSFixed64(const string& str) {
      WrappedMessage wm;
      fromProtoSerialization(str, wm);
 
      if (!wm.has_wrappedsfixed64()) {
        throw MarshallingException("Not sfixed64!");
      }

      return wm.wrappedsfixed64();
    }

    int64 unmarshalSInt64(const string& str) {
      WrappedMessage wm;
      fromProtoSerialization(str, wm);
 
      if (!wm.has_wrappedsint64()) {
        throw MarshallingException("Not sint64!");
      }

      return wm.wrappedsint64();
    }

    uint64 unmarshalUInt64(const string& str) {
      WrappedMessage wm;
      fromProtoSerialization(str, wm);
 
      if (!wm.has_wrappeduint64()) {
        throw MarshallingException("Not uint64!");
      }

      return wm.wrappeduint64();
    }

    uint64 unmarshalFixed64(const string& str) {
      WrappedMessage wm;
      fromProtoSerialization(str, wm);
 
      if (!wm.has_wrappedsfixed64()) {
        throw MarshallingException("Not fixed64!");
      }

      return wm.wrappedfixed64();
    }

    bool unmarshalBool(const string& str) {
      WrappedMessage wm;
      fromProtoSerialization(str, wm);
 
      if (!wm.has_wrappedbool()) {
        throw MarshallingException("Not bool!");
      }

      return wm.wrappedbool();
    }


    string unmarshalString(const string& str) {
      WrappedMessage wm;
      fromProtoSerialization(str, wm);

      if (!wm.has_wrappedstring()) {
        throw MarshallingException("Not string!");
      }

      return wm.wrappedstring();
    }

    string unmarshalBytes(const string& str) {
      WrappedMessage wm;
      fromProtoSerialization(str, wm);

      if (!wm.has_wrappedbytes()) {
        throw MarshallingException("Not bytes!");
      }

      return wm.wrappedbytes();
    }
  }
}
