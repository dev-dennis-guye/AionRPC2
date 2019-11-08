<#import "../java_macros.ftl" as macros>
package org.aion.rpc.types;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.aion.rpc.errors.RPCExceptions.InvalidParamsRPCException;
import org.aion.rpc.errors.RPCExceptions.ParseErrorRPCException;
import org.aion.rpc.types.RPCTypes.*;
import org.aion.types.AionAddress;
import org.aion.util.bytes.ByteUtil;
import org.aion.util.types.ByteArrayWrapper;
import org.json.JSONArray;
import org.json.JSONObject;

/******************************************************************************
*
* AUTO-GENERATED SOURCE FILE.  DO NOT EDIT MANUALLY -- YOUR CHANGES WILL
* BE WIPED OUT WHEN THIS FILE GETS RE-GENERATED OR UPDATED.
* GENERATED: ${date}
*
*****************************************************************************/
public class RPCTypesConverter{
    <#list patterns as pattern >
    private static final Pattern ${pattern.name} = Pattern.compile("${pattern.regex}");
    </#list>

    public static class ${macros.toJavaConverterFromName("any")}{

        public static String decode(Object s){
            if(s==null || s.equals(JSONObject.NULL)) return null;
            return s.toString();
        }

        public static Object encode(Object obj){
            return obj;
        }
    }

    public static class ${macros.toJavaConverterFromName("string")}{

        public static String decode(Object s){
            if(s==null || s.equals(JSONObject.NULL)) return null;
            return s.toString();
        }

        public static String encode(String s){
            return s;
        }
    }

    public static class ${macros.toJavaConverterFromName("bool")}{
        public static Boolean decode(Object s){
            if ( s!=null && !s.equals(JSONObject.NULL) && booleanPattern.matcher(s.toString()).find()) return Boolean.parseBoolean(s.toString());
            else throw ParseErrorRPCException.INSTANCE;
        }

        public static Boolean encode(Boolean b){
            return b;
        }
    }

    public static class ${macros.toJavaConverterFromName("byte")}{
        public static Byte decode(Object s){
            if(s==null||s.equals(JSONObject.NULL)) return null;
            if(hexPattern.matcher(s.toString()).find()){
                return Byte.parseByte(s.toString().substring(2), 16);
            }
            else if(decPattern.matcher(s.toString()).find()){
                return Byte.parseByte(s.toString());
            }
            else{
                throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
            }
        }

        public static Byte encode(Byte s) {
            try {
                return s;
            } catch (Exception e) {
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }

        public static String encodeHex(Byte s) {
            try {
                if (s==null||s.equals(JSONObject.NULL)) return null;
                else return "0x"+ByteUtil.oneByteToHexString(s);
            } catch (Exception e) {
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }
    }

    public static class ${macros.toJavaConverterFromName("long")}{

        public static Long decode(Object s){
            if(s==null || s.equals(JSONObject.NULL)) return null;
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
            if (s==null || s.equals(JSONObject.NULL)) return null;
            else return "0x"+Long.toHexString(s);
            }catch (Exception e){
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }

    }


    public static class ${macros.toJavaConverterFromName("int")}{

        public static Integer decode(Object s){
            if(s==null || s.equals(JSONObject.NULL)) return null;
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
                if (s==null) return null;
                else return "0x"+Integer.toHexString(s);
            }catch (Exception e){
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }
    }

    public static class ${macros.toJavaConverterFromName("bigint")}{

        public static String encodeHex(BigInteger bigInteger){
            try{
                if(bigInteger==null) return null;
                return "0x"+bigInteger.toString(16);
            } catch (Exception e){
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }

        public static String encode(BigInteger bigInteger){
            try{
                if(bigInteger==null) return null;
                return bigInteger.toString(10);
            } catch(Exception e){
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }

        public static BigInteger decode(Object s){
            if(s==null || s.equals(JSONObject.NULL)) return null;

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

        public static ByteArray decode(Object obj){
            if (obj == null || obj.equals(JSONObject.NULL)){
                return null;
            }
            else if(obj instanceof byte[]){
                return new ByteArray((byte[]) obj);
            }
            else if (obj instanceof String && byteArrayPattern.matcher(((String)obj)).find()){
                return new ByteArray(ByteUtil.hexStringToBytes((String) obj));
            }
            else {
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }

        public static String encode(ByteArray bytes){
            if (bytes == null) return null;
            else return bytes.toString();
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
<#list unionTypes as unionType>
    public static class ${macros.toJavaConverter(unionType)}{
        public static ${macros.toJavaType(unionType)} decode(Object str){
            <#if unionType.nullable=="true"> if(str==null|| str==JSONObject.NULL) return null;</#if>
            return ${macros.toJavaType(unionType)}.decode(str);
        }

        public static Object encode(${macros.toJavaType(unionType)} obj){
            if(obj==null) return null;
            else return obj.encode();
        }

        public static String encodeStr(${macros.toJavaType(unionType)} obj){
            if(obj==null) return null;
            else return obj.encode().toString();
        }
    }

</#list>
<#list compositeTypes as compositeType>
    public static class ${macros.toJavaConverter(compositeType)}{
        public static ${macros.toJavaType(compositeType)} decode(Object str){
            try{
                if(str==null || str.equals(JSONObject.NULL)) return null;
                JSONObject jsonObject = str instanceof JSONObject? (JSONObject)str :new JSONObject(str.toString());
                return new ${macros.toJavaType(compositeType)}(<#list compositeType.fields as field> ${macros.toJavaConverter(field.type)}.decode(jsonObject.opt("${field.fieldName}")) <#if field_has_next>,</#if></#list>);
            } catch (Exception e){
                throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
            }
        }

        public static String encodeStr( ${macros.toJavaType(compositeType)} obj){
            try{
                if(obj==null) return null;
                JSONObject jsonObject = new JSONObject();
                <#list compositeType.fields as field>
                jsonObject.put("${field.fieldName}", obj.${field.fieldName}==null? JSONObject.NULL:${macros.toJavaConverter(field.type)}.encode(obj.${field.fieldName}));
                </#list>
                return jsonObject.toString();
            }
            catch (Exception e){
                throw ${macros.toJavaException(encodeError.error_class)}.INSTANCE;
            }
        }

        public static Object encode( ${macros.toJavaType(compositeType)} obj){
            try{
                if(obj==null) return null;
                JSONObject jsonObject = new JSONObject();
                <#list compositeType.fields as field>
                jsonObject.put("${field.fieldName}", ${macros.toJavaConverter(field.type)}.encode(obj.${field.fieldName}));
                </#list>
                return jsonObject;
            }catch (Exception e){
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
                if(object==null || object.equals(JSONObject.NULL)) return null;
                else if (checkConstraints(object.toString())){
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
                <#if "${macros.toJavaType(constrainedType)}"=="String" || "${macros.toJavaType(constrainedType)}"=="ByteArray">
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
                return null;
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
            if(object==null || object.equals(JSONObject.NULL)) <#if paramType.fields?has_content>return null;<#else>return new ${macros.toJavaType(paramType)}();</#if>
            String s = object.toString();
            try{<#if paramType.fields?has_content>
                ${macros.toJavaType(paramType)} obj;
                if(s.startsWith("[") && s.endsWith("]")){
                    JSONArray jsonArray = new JSONArray(s);
                    if(jsonArray.length() > ${paramType.fields?size}) throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
                    else obj = new ${macros.toJavaType(paramType)}(<#list paramType.fields as param> ${macros.toJavaConverter(param.type)}.decode(jsonArray.opt(${param.index}))<#if param_has_next>,</#if></#list>);
                }
                else if(s.startsWith("{") && s.endsWith("}")){
                    JSONObject jsonObject = new JSONObject(s);
                    if(jsonObject.keySet().size() > ${paramType.fields?size}) throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
                    else obj = new ${macros.toJavaType(paramType)}(<#list paramType.fields as param> ${macros.toJavaConverter(param.type)}.decode(jsonObject.opt("${param.fieldName}"))<#if param_has_next>,</#if></#list>);
                }
                else{
                    throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
                }
                return obj;<#else >
                if(s.equals("[]") || s.equals("{}")) {//TODO This may not be the best way to handle an empty param list
                    return new ${macros.toJavaType(paramType)}();
                }
                else{
                    throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
                }</#if>
            }
            catch(Exception e){
                throw ${macros.toJavaException("InvalidParams")}.INSTANCE;
            }
        }

        public static Object encode(${macros.toJavaType(paramType)} obj){
            try{
               <#if paramType.fields?has_content>
                JSONArray arr = new JSONArray();
                <#list paramType.fields as param>
                arr.put(${param.index}, obj.${param.fieldName}==null? JSONObject.NULL : ${macros.toJavaConverter(param.type)}.encode(obj.${param.fieldName}));
                </#list>
                return arr;
                <#else >
                return null;
                </#if>
            }catch(Exception e){
                throw ${macros.toJavaException(decodeError.error_class)}.INSTANCE;
            }
        }
    }

</#list>
<#list enumTypes as enum>
    public static class ${macros.toJavaConverter(enum)}{
        public static ${macros.toJavaType(enum)} decode(Object object){
            if(object==null || object.equals(JSONObject.NULL)) return null;
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
            if(object == null || object.equals(JSONObject.NULL)) return null;
            JSONArray arr = new JSONArray(object.toString());
            ${macros.toJavaType(arrayType)} temp = new ArrayList<>();
            for(int i=0; i < arr.length(); i++){
                temp.add(${macros.toJavaConverter(arrayType.nestedType)}.decode(arr.opt(i)));
            }
            return Collections.unmodifiableList(temp);
        }

        public static Object encode(${macros.toJavaType(arrayType)} list){
            if(list==null) return null;
            JSONArray arr = new JSONArray();

            for(int i=0; i < list.size();i++){
                arr.put(${macros.toJavaConverter(arrayType.nestedType)}.encode(list.get(i)));
            }
            return arr;
        }

        public static String encodesStr(${macros.toJavaType(arrayType)} list){
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
