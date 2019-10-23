package org.aion.rpcgenerator;

import java.util.List;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Config {

    public static class TemplateName{
        public final String serverFile;
        public final String clientFile;

        public TemplateName(String serverFile, String clientFile) {
            this.serverFile = serverFile;
            this.clientFile = clientFile;
        }
    }

    private final String errorDefinition;
    private final String typeDefinition;
    private final String rpcDefinitionSuffix;
    public Config(Document document){
        Element root = document.getDocumentElement();
        Element definitions = XMLUtils.elementFromTag(root, "definition").orElseThrow();
        errorDefinition = XMLUtils.elementFromTag(root, "error").map(Node::getNodeValue).orElseThrow();
        typeDefinition = XMLUtils.elementFromTag(root, "rpc").map(Node::getNodeValue).orElseThrow();
        rpcDefinitionSuffix =  XMLUtils.elementFromTag(root, "type").map(Node::getNodeValue).orElseThrow();

    }



}
