<#import "../java_macros.ftl" as macros>
package org.aion.api.server.rpc3.types;

import org.json.JSONObject;
import static org.aion.api.server.rpc3.types.RPCTypes.*;

public class RPCTypeConverter{

    public static StringConverter{

        public static String decode(Object s){
            if(s==null) return null;
            return s.toString();
        }

        public String encode(String s){
            return s;
        }
    }

    public static LongConverter{
        private final Pattern hexPattern = Pattern.compile("^0x[0-9a-fA-F]+");
        private final Pattern decPattern = Pattern.compile("^[0-9]+");

        public static Long decode(Object s){
            if(s==null) return null;
            if(hexPattern.matcher(s.toString()).find()){
                return Long.parseLong(s.toString().subString(2), 16);
            }
            else if(decPattern.matcher(s.toString()).find()){
                return Long.parseLong(s.toString());
            }
            else{
                throw new ${macros.toJavaException(decodeError.error_class)}();
            }
        }

        public static Long encode(Long s){
            try{
                return s.toString();
            }catch (Exception e){
                throw new ${macros.toJavaException(encodeError.error_class)}();
            }
        }

        public static Long encodeHex(Long s){
            try{
                return "0x"+Long.toHexString(s);
            }catch (Exception e){
                throw new ${macros.toJavaException(encodeError.error_class)}();
            }
        }

    }


    public static IntegerConverter{
        private final static Pattern hexPattern = Pattern.compile("^0x[0-9a-fA-F]+");
        private final static Pattern decPattern = Pattern.compile("^[0-9]+");

        public static Integer decode(Object s){
            if(s==null) return null;
            if(hexPattern.matcher(s.toString()).find()){
                return Integer.parseInteger(s.toString().subString(2), 16);
            }
            else if(decPattern.matcher(s.toString()).find()){
                return Integer.parseInteger(s.toString());
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

    public static BigIntegerConverter{
        private final static Pattern hexPattern = Pattern.compile("^0x[0-9a-fA-F]+");
        private final static Pattern decPattern = Pattern.compile("^[0-9]+");

        public static String encodeHex(BigInteger bigInteger){
            try{
                return "0x"+bigInteger.toString();
            } catch (Exception e){
                throw new ${macros.toJavaException(encodeError.error_class)}();
            }
        }

        public static String encode(BigInteger bigInteger){
            try{
                return bigInteger.toString();
            } catch(Exception e){
                throw new ${macros.toJavaException(encodeError.error_class)}();
            }
        }

        public static BigInteger decode(Object s){
            if(s==null) return null;

            if(hexPattern.matcher(s.toString()).find()){
                return new BigInteger(s.toString().subString(2), 16);
            }
            else if(decPattern.matcher(s.toString()).find()){
                return new BigInteger(s.toString());
            }
            else{
                throw new ${macros.toJavaException(decodeError.error_class)}();
            }
        }
    }

<#list compositeTypes as compositeType>
    public static class ${macros.toJavaConverter(compositeType.name)}{
        public static ${macros.toJavaType(compositeType.name)} decode(Object str){
            try{
                JSONObject jsonObject = new JSONObject(str);
                ${macros.toJavaType(compositeType.name)} obj = new ${macros.toJavaType(compositeType.name)}();
                <#list compositeType.fields as field>
                obj.set${field.fieldName}(${macros.toJavaConverter(field.type.name)}.decode(jsonObject.opt("${field.fieldName}")));
                </#list>
            } catch (Exception e){
                throw new ${macros.toJavaException(decodeError.error_class)}();
            }
        }

        public static String encode( ${macros.toJavaType(compositeType.name)} obj){
            try{
                if(obj==null) return null;
                JSONObject jsonObject = new JSONObject();
                <#list compositeType.fields as field>
                jsonObject.put("${field.fieldName}", ${macros.toJavaConverter(field.type.name)}.encode(obj.get${field.fieldName}()));
                </#list>
                return jsonObject.toString();
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

        public static ${macros.toJavaType(constrainedType.name)} decode(Object object){
            try{
                if (object==null && checkConstraints(s)){
                    return ${macros.toJavaConverter(constrainedType.baseType.name)}.decode(object);
                }
                else{
                    throw new ${macros.toJavaException(decodeError.error_class)}();
                }
            } catch(Exception e){
                throw new ${macros.toJavaException(decodeError.error_class)}();
            }
        }

        public static String encode(${macros.toJavaType(constrainedType.name)} obj){
            if (obj != null){
                <#if "${constrainedType.baseType.name}" == "string">
                ${macros.toJavaType(constrainedType.baseType.name)} result = ${macros.toJavaConverter(constrainedType.baseType.name)}.encode(obj);
                <#else>
                ${macros.toJavaType(constrainedType.baseType.name)} result = ${macros.toJavaConverter(constrainedType.baseType.name)}.encodeHex(obj);
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

        private boolean checkConstraints(String s){
            return regex.matcher(s).find() && s.length() >= ${constrainedType.min} && s.length() <= ${constrainedType.max};
        }
    }

</#list>
<#list paramTypes as paramType>
    public static class ${macros.toJavaConverter(paramType.name)}{
        public static ${macros.toJavaType(paramType.name)} decode(Object object){
            String s = object.toString();
            try{
            ${macros.toJavaType(paramType.name)} obj = new ${macros.toJavaType(paramType.name)}();
                if(s.startsWith("[") && s.endsWith("]")){
                    JSONArray jsonArray = new JSONArray(s);<#list paramType.fields as param>
                    obj.set${param.fieldName}(${macros.toJavaConverter(param.type.name)}.decode(jsonArray.opt(${param.index})));
                    </#list>
                }
                else if(s.startsWith("{") && s.endsWith("}")){
                    JSONObject object = new JSONObject(s);<#list paramType.fields as param>
                    obj.set${param.fieldName}(${macros.toJavaConverter(param.type.name)}.decode(jsonObject.opt(${param.fieldName})));
                    </#list>
                }
                else{
                    throw new ${macros.toJavaException(decodeError.error_class)};
                }
                return obj;
            }catch(Exception e){
                throw new ${macros.toJavaException(decodeError.error_class)};
            }
        }

        public static String encode(${macros.toJavaType(paramType.name)} obj){
            try{JSONArray arr = new JSONArray();
                <#list paramType.fields as param>
                arr.put(${param.index}, ${macros.toJavaConverter(param.type.name)}.encode(obj.get${param.fieldName}())
                </#list>
            }catch(Exception e){
                throw new ${macros.toJavaException(decodeError.error_class)};
            }
        }
    }
</#list>

<#list enumTypes as enum>
    public static class ${macros.toJavaConverter(enum.name)}{
        public static ${macros.toJavaType(enum.name)} decode(Object object){
            if(object==null) return null;
            return ${macros.toJavaType(enum.name)}.fromString(object.toString);
        }

        public static String encode(${macros.toJavaType(enum.name)} obj){
            if(obj==null) return null;
            return obj.x;
        }
    }
</#list>
}