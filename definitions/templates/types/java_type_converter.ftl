<#import "../java_macros.ftl" as macros>
package org.aion.api.server.rpc3.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.aion.api.server.rpc3.types.RPCTypes.Error;
import org.aion.types.AionAddress;
import org.aion.util.bytes.ByteUtil;
import org.aion.util.types.ByteArrayWrapper;
import org.aion.api.server.rpc3.types.RPCTypes.*;
import java.util.regex.Pattern;
import org.aion.api.server.rpc3.RPCExceptions.ParseErrorRPCException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.math.BigInteger;

/******************************************************************************
*
* AUTO-GENERATED SOURCE FILE.  DO NOT EDIT MANUALLY -- YOUR CHANGES WILL
* BE WIPED OUT WHEN THIS FILE GETS RE-GENERATED OR UPDATED.
*
*****************************************************************************/
public class RPCTypesConverter{

    private static final Pattern hexPattern= Pattern.compile("^0x[0-9a-fA-F]+");
    private static final Pattern decPattern = Pattern.compile("^-?[0-9]+");

    public static class ${macros.toJavaConverterFromName("any")}{

        public static String decode(Object s){
            if(s==null) return null;
            return s.toString();
        }

        public static Object encode(Object obj){
            return obj;
        }
    }

    public static class ${macros.toJavaConverterFromName("string")}{

        public static String decode(Object s){
            if(s==null) return null;
            return s.toString();
        }

        public static String encode(String s){
            return s;
        }
    }

    public static class ${macros.toJavaConverterFromName("long")}{

        public static Long decode(Object s){
            if(s==null) return null;
            if(hexPattern.matcher(s.toString()).find()){
                return Long.parseLong(s.toString().substring(2), 16);
            }
            else if(decPattern.matcher(s.toString()).find()){
                return Long.parseLong(s.toString());
            }
            else{
                throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
            }
        }

        public static Long encode(Long s){
            try{
                return s;
            }catch (Exception e){
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }

        public static String encodeHex(Long s){
            try{
                return "0x"+Long.toHexString(s);
            }catch (Exception e){
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }

    }


    public static class ${macros.toJavaConverterFromName("int")}{

        public static Integer decode(Object s){
            if(s==null) return null;
            if(hexPattern.matcher(s.toString()).find()){
                return Integer.parseInt(s.toString().substring(2), 16);
            }
            else if(decPattern.matcher(s.toString()).find()){
                return Integer.parseInt(s.toString());
            }
            else{
                throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
            }
        }

        public static Integer encode(Integer s){
            try{
                return s;
            }catch (Exception e){
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }

        public static String encodeHex(Integer s){
            try{
                return "0x"+Integer.toHexString(s);
            }catch (Exception e){
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }
    }

    public static class ${macros.toJavaConverterFromName("bigint")}{

        public static String encodeHex(BigInteger bigInteger){
            try{
                return "0x"+bigInteger.toString(16);
            } catch (Exception e){
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }

        public static String encode(BigInteger bigInteger){
            try{
                return bigInteger.toString(16);
            } catch(Exception e){
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }

        public static BigInteger decode(Object s){
            if(s==null) return null;

            if(hexPattern.matcher(s.toString()).find()){
                return new BigInteger(s.toString().substring(2), 16);
            }
            else if(decPattern.matcher(s.toString()).find()){
                return new BigInteger(s.toString());
            }
            else{
                throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
            }
        }
    }

    public static class ${macros.toJavaConverterFromName("byte-array")}{

        public static ByteArrayWrapper decode(Object obj){
            if (obj == null){
                return null;
            }
            else if(obj instanceof byte[]){
                return ByteArrayWrapper.wrap(((byte[])obj));
            }
            else if (obj instanceof String){
                if (hexPattern.matcher(((String)obj)).find()){
                    return ByteArrayWrapper.wrap(ByteUtil.hexStringToBytes((String) obj));
                } else {
                    return ByteArrayWrapper.wrap(((String)obj).getBytes());
                }
            }
            else {
                    throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }

        public static String encode(ByteArrayWrapper bytes){
            if (bytes == null) return null;
            else return "0x" + bytes.toString();
        }
    }

    public static class ${macros.toJavaConverterFromName("address")}{
        public static AionAddress decode(Object obj){
            try{
                if (obj == null){
                    return null;
                }
                else if (obj instanceof String && hexPattern.matcher(((String)obj)).find()){
                    return new AionAddress(ByteUtil.hexStringToBytes(((String) obj)));
                }
                else if (obj instanceof byte[]){
                    return new AionAddress(((byte[])obj));
                }
                else {
                    throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
                }
            }catch (Exception e){
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }

        public static String encode(AionAddress address){
            if (address==null) return null;
            else return "0x"+address.toString();
        }
    }

<#list compositeTypes as compositeType>
    public static class ${macros.toJavaConverter(compositeType)}{
        public static ${macros.toJavaType(compositeType)} decode(Object str){
            try{
                if(str==null) return null;
                JSONObject jsonObject = new JSONObject(((String) str).replaceAll("\"","\""));
                return new ${macros.toJavaType(compositeType)}(<#list compositeType.fields as field> ${macros.toJavaConverter(field.type)}.decode(jsonObject.opt("${field.fieldName}")) <#if field_has_next>,</#if></#list>);
            } catch (Exception e){
                throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
            }
        }

        public static String encode( ${macros.toJavaType(compositeType)} obj){
            try{
                if(obj==null) return null;
                JSONObject jsonObject = new JSONObject();
                <#list compositeType.fields as field>
                jsonObject.put("${field.fieldName}", ${macros.toJavaConverter(field.type)}.encode(obj.${field.fieldName}));
                </#list>
                return jsonObject.toString().replaceAll("\"","\"");
            }
            catch (Exception e){
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }

    }

</#list>
<#list constrainedTypes as constrainedType>
    public static class ${macros.toJavaConverter(constrainedType)}{
        private static final Pattern regex = Pattern.compile("${constrainedType.regex}");

        public static ${macros.toJavaType(constrainedType)} decode(Object object){
            try{
                if (object!=null && checkConstraints(object.toString())){
                    return ${macros.toJavaConverter(constrainedType.baseType)}.decode(object);
                }
                else{
                    throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
                }
            } catch(Exception e){
                throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
            }
        }

        public static String encode(${macros.toJavaType(constrainedType)} obj){
            if (obj != null){
                <#if "${macros.toJavaType(constrainedType)}"=="String" || "${macros.toJavaType(constrainedType)}"=="ByteArrayWrapper">
                String result = ${macros.toJavaConverter(constrainedType.baseType)}.encode(obj);
                <#else>
                String result = ${macros.toJavaConverter(constrainedType.baseType)}.encodeHex(obj);
                </#if>
                if(checkConstraints(result))
                    return result;
                else
                    throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
            else{
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }

        private static boolean checkConstraints(String s){
            return regex.matcher(s).find() && s.length() >= ${constrainedType.min} && s.length() <= ${constrainedType.max};
        }
    }

</#list>
<#list paramTypes as paramType>
    public static class ${macros.toJavaConverter(paramType)}{
        public static ${macros.toJavaType(paramType)} decode(Object object){
            if(object==null) return null;
            String s = object.toString().replaceAll("\"","\"");
            try{
                ${macros.toJavaType(paramType)} obj;
                if(s.startsWith("[") && s.endsWith("]")){
                    JSONArray jsonArray = new JSONArray(s);
                    obj = new ${macros.toJavaType(paramType)}(<#list paramType.fields as param> ${macros.toJavaConverter(param.type)}.decode(jsonArray.opt(${param.index}))<#if param_has_next>,</#if></#list>);
                }
                else if(s.startsWith("{") && s.endsWith("}")){
                    JSONObject jsonObject = new JSONObject(s);
                    obj = new ${macros.toJavaType(paramType)}(<#list paramType.fields as param> ${macros.toJavaConverter(param.type)}.decode(jsonObject.opt("${param.fieldName}"))<#if param_has_next>,</#if></#list>);
                }
                else{
                    throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
                }
                return obj;
            }catch(Exception e){
                throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
            }
        }

        public static String encode(${macros.toJavaType(paramType)} obj){
            try{
                JSONArray arr = new JSONArray();
                <#list paramType.fields as param>
                arr.put(${param.index}, ${macros.toJavaConverter(param.type)}.encode(obj.${param.fieldName}));
                </#list>return arr.toString().replaceAll("\"","\"");
            }catch(Exception e){
                throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
            }
        }
    }

</#list>
<#list enumTypes as enum>
    public static class ${macros.toJavaConverter(enum)}{
        public static ${macros.toJavaType(enum)} decode(Object object){
            if(object==null) return null;
            return ${macros.toJavaType(enum)}.fromString(object.toString());
        }

        public static String encode(${macros.toJavaType(enum)} obj){
            if(obj==null) return null;
            return obj.x;
        }
    }

</#list>
<#list arrayTypes as arrayType>
    public static class ${macros.toJavaConverter(arrayType)}{
        public static ${macros.toJavaType(arrayType)} decode(Object object){
            if(object == null) return null;
            JSONArray arr = new JSONArray(object.toString());
            ${macros.toJavaType(arrayType)} temp = new ArrayList<>();
            for(int i=0; i < arr.length(); i++){
                temp.add(${macros.toJavaConverter(arrayType.nestedType)}.decode(arr.opt(i)));
            }
            return Collections.unmodifiableList(temp);
        }

        public static String encode(${macros.toJavaType(arrayType)} list){
            if(list==null) return null;
            JSONArray arr = new JSONArray();

            for(int i=0; i < list.size();i++){
                arr.put(${macros.toJavaConverter(arrayType.nestedType)}.encode(list.get(i)));
            }
            return arr.toString();
        }
    }

</#list>
}
