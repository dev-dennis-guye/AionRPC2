<#import "../java_macros.ftl" as macros>
package org.aion.api.server.rpc3.types;

import org.aion.types.AionAddress;
import org.aion.util.bytes.ByteUtil;
import org.aion.util.types.ByteArrayWrapper;
import static org.aion.api.server.rpc3.types.RPCTypes.*;
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
    private static final Pattern decPattern = Pattern.compile("^[0-9]+");

    public static class ${macros.toJavaConverter("string")}{

        public static String decode(Object s){
            if(s==null) return null;
            return s.toString();
        }

        public static String encode(String s){
            return s;
        }
    }

    public static class ${macros.toJavaConverter("long")}{
        private static final Pattern hexPattern = Pattern.compile("^0x[0-9a-fA-F]+");
        private static final Pattern decPattern = Pattern.compile("^[0-9]+");

        public static Long decode(Object s){
            if(s==null) return null;
            if(hexPattern.matcher(s.toString()).find()){
                return Long.parseLong(s.toString().substring(2), 16);
            }
            else if(decPattern.matcher(s.toString()).find()){
                return Long.parseLong(s.toString());
            }
            else{
                throw new ${macros.toJavaException(decodeError.error_class)}();
            }
        }

        public static String encode(Long s){
            try{
                return s.toString();
            }catch (Exception e){
                throw new ${macros.toJavaException(encodeError.error_class)}();
            }
        }

        public static String encodeHex(Long s){
            try{
                return "0x"+Long.toHexString(s);
            }catch (Exception e){
                throw new ${macros.toJavaException(encodeError.error_class)}();
            }
        }

    }


    public static class ${macros.toJavaConverter("int")}{
        private final static Pattern hexPattern = Pattern.compile("^0x[0-9a-fA-F]+");
        private final static Pattern decPattern = Pattern.compile("^[0-9]+");

        public static Integer decode(Object s){
            if(s==null) return null;
            if(hexPattern.matcher(s.toString()).find()){
                return Integer.parseInt(s.toString().substring(2), 16);
            }
            else if(decPattern.matcher(s.toString()).find()){
                return Integer.parseInt(s.toString());
            }
            else{
                throw new ${macros.toJavaException(decodeError.error_class)}();
            }
        }

        public static String encode(Integer s){
            try{
                return s.toString();
            }catch (Exception e){
                throw new ${macros.toJavaException(encodeError.error_class)}();
            }
        }

        public static String encodeHex(Integer s){
            try{
                return "0x"+Integer.toHexString(s);
            }catch (Exception e){
                throw new ${macros.toJavaException(encodeError.error_class)}();
            }
        }
    }

    public static class ${macros.toJavaConverter("bigint")}{

        public static String encodeHex(BigInteger bigInteger){
            try{
                return "0x"+bigInteger.toString(16);
            } catch (Exception e){
                throw new ${macros.toJavaException(encodeError.error_class)}();
            }
        }

        public static String encode(BigInteger bigInteger){
            try{
                return bigInteger.toString(16);
            } catch(Exception e){
                throw new ${macros.toJavaException(encodeError.error_class)}();
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
                throw new ${macros.toJavaException(decodeError.error_class)}();
            }
        }
    }

    public static class ${macros.toJavaConverter("byte-array")}{
        private static final Pattern hexPattern = Pattern.compile("^0x[0-9a-fA-F]+");

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
                    throw new ${macros.toJavaException(encodeError.error_class)}();
            }
        }

        public static String encode(ByteArrayWrapper bytes){
            if (bytes == null) return null;
            else return "0x" + bytes.toString();
        }
    }

    public static class ${macros.toJavaConverter("address")}{
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
                    throw new ${macros.toJavaException(encodeError.error_class)}();
                }
            }catch (Exception e){
                throw new ${macros.toJavaException(encodeError.error_class)}();
            }
        }

        public static String encode(AionAddress address){
            if (address==null) return null;
            else return "0x"+address.toString();
        }
    }

<#list compositeTypes as compositeType>
    public static class ${macros.toJavaConverter(compositeType.name)}{
        public static ${macros.toJavaType(compositeType)} decode(Object str){
            try{
                JSONObject jsonObject = new JSONObject(((String) str).replaceAll("\"","\""));
                return new ${macros.toJavaType(compositeType)}(<#list compositeType.fields as field> ${macros.toJavaConverter(field.type.name)}.decode(jsonObject.opt("${field.fieldName}")) <#if field_has_next>,</#if></#list>);
            } catch (Exception e){
                throw new ${macros.toJavaException(decodeError.error_class)}();
            }
        }

        public static String encode( ${macros.toJavaType(compositeType)} obj){
            try{
                if(obj==null) return null;
                JSONObject jsonObject = new JSONObject();
                <#list compositeType.fields as field>
                jsonObject.put("${field.fieldName}", ${macros.toJavaConverter(field.type.name)}.encode(obj.${field.fieldName}));
                </#list>
                return jsonObject.toString().replaceAll("\"","\"");
            }
            catch (Exception e){
                throw new ${macros.toJavaException(encodeError.error_class)}();
            }
        }

    }

</#list>
<#list constrainedTypes as constrainedType>
    public static class ${macros.toJavaConverter(constrainedType.name)}{
        private static final Pattern regex = Pattern.compile("${constrainedType.regex}");

        public static ${macros.toJavaType(constrainedType)} decode(Object object){
            try{
                if (object!=null && checkConstraints(object.toString())){
                    return ${macros.toJavaConverter(constrainedType.baseType.name)}.decode(object);
                }
                else{
                    throw new ${macros.toJavaException(decodeError.error_class)}();
                }
            } catch(Exception e){
                throw new ${macros.toJavaException(decodeError.error_class)}();
            }
        }

        public static String encode(${macros.toJavaType(constrainedType)} obj){
            if (obj != null){
                <#if "${macros.toJavaType(constrainedType)}"=="String" || "${macros.toJavaType(constrainedType)}"=="ByteArrayWrapper">
                String result = ${macros.toJavaConverter(constrainedType.baseType.name)}.encode(obj);
                <#else>
                String result = ${macros.toJavaConverter(constrainedType.baseType.name)}.encodeHex(obj);
                </#if>
                if(checkConstraints(result))
                    return result;
                else
                    throw new ${macros.toJavaException(encodeError.error_class)}();
            }
            else{
                throw new ${macros.toJavaException(encodeError.error_class)}();
            }
        }

        private static boolean checkConstraints(String s){
            return regex.matcher(s).find() && s.length() >= ${constrainedType.min} && s.length() <= ${constrainedType.max};
        }
    }

</#list>
<#list paramTypes as paramType>
    public static class ${macros.toJavaConverter(paramType.name)}{
        public static ${macros.toJavaType(paramType)} decode(Object object){
            String s = object.toString().replaceAll("\"","\"");
            try{
                ${macros.toJavaType(paramType)} obj;
                if(s.startsWith("[") && s.endsWith("]")){
                    JSONArray jsonArray = new JSONArray(s);
                    obj = new ${macros.toJavaType(paramType)}(<#list paramType.fields as param> ${macros.toJavaConverter(param.type.name)}.decode(jsonArray.opt(${param.index}))<#if param_has_next>,</#if></#list>);
                }
                else if(s.startsWith("{") && s.endsWith("}")){
                    JSONObject jsonObject = new JSONObject(s);
                    obj = new ${macros.toJavaType(paramType)}(<#list paramType.fields as param> ${macros.toJavaConverter(param.type.name)}.decode(jsonObject.opt("${param.fieldName}"))<#if param_has_next>,</#if></#list>);
                }
                else{
                    throw new ${macros.toJavaException(decodeError.error_class)}();
                }
                return obj;
            }catch(Exception e){
                throw new ${macros.toJavaException(decodeError.error_class)}();
            }
        }

        public static String encode(${macros.toJavaType(paramType)} obj){
            try{
                JSONArray arr = new JSONArray();
                <#list paramType.fields as param>
                arr.put(${param.index}, ${macros.toJavaConverter(param.type.name)}.encode(obj.${param.fieldName}));
                </#list>return arr.toString().replaceAll("\"","\"");
            }catch(Exception e){
                throw new ${macros.toJavaException(decodeError.error_class)}();
            }
        }
    }

</#list>
<#list enumTypes as enum>
    public static class ${macros.toJavaConverter(enum.name)}{
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
}
