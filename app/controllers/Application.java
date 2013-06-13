package controllers;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.axis2.AxisFault;

import play.libs.F.Function;
import play.libs.WS;
import play.libs.XPath;
import play.mvc.*;
import play.mvc.BodyParser.Xml;
import play.templates.TemplateMagic;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import views.html.index;
import views.xml.*;

import com.webservice.GlobalWeatherStub;
import com.webservice.GlobalWeatherStub.GetWeather;
import com.webservice.GlobalWeatherStub.GetWeatherResponse;

public class Application extends Controller {
  
	@BodyParser.Of(Xml.class)
	public static Result index() {
		/*
		 * Use of axis2
		 * 
		 * client stub generation sample :
		 * /wsdl2java.sh -uri "http://www.webservicex.com/globalweather.asmx?WSDL" -p com.webservice -d adb -s 
		 */
/*
		GlobalWeatherStub stub;
		String responseStr ="";
		try {
			stub = new GlobalWeatherStub("http://www.webservicex.com/globalweather.asmx");
			GetWeather myRequest = new GetWeather();

			myRequest.setCityName("Rouen");
			myRequest.setCountryName("France");

			GetWeatherResponse response = stub.getWeather(myRequest);

			responseStr = response.getGetWeatherResult();
			
			//System.out.println(responseStr);
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
		/*
		 * Pure play philosophy WS call
		 * => use of non blocking IO to the SOAP Webservice
		 * 
		 * But, as Play does not support natively SOAP message
		 * We have to manage the soap message "à la main"
		 * 1 - SOAP request is in an XML template
		 * 2 - the response is parsed as a DOM document with XPath
		 * 
		 */
		
		return async(
				      WS.url("http://www.webservicex.com/globalweather.asmx").setContentType("text/xml").post(getWeatherSoapEnveloppe.render("Rouen", "France").body()).map(
				        new Function<WS.Response, Result>() {
				          public Result apply(WS.Response response) {
				        	  //System.out.println("--"+response.getBody());
				        	  Document dom = response.asXml();
				        	  System.out.println("1-----"+dom.getNodeName());
				        	  System.out.println("2-----"+dom.getFirstChild().getNodeName());
				        	  System.out.println("3-----"+dom.getFirstChild().getFirstChild().getNodeName());
				        	  System.out.println("4-----"+dom.getFirstChild().getFirstChild().getFirstChild().getNodeName());
				        	  System.out.println("5-----"+dom.getFirstChild().getFirstChild().getFirstChild().getFirstChild().getNodeName());
				        	  
				        	  Map<String, String> nameSpaceList = new HashMap<String, String> () {
				        		  {
				        			  put("wea", "http://www.webserviceX.NET");
				        			  put("soap", "http://www.w3.org/2003/05/soap-envelope");
				        		  }
				        	  };
				        	  
				        	  
//				        	  NodeList GetWeatherResultNode = XPath.selectNodes("GetWeatherResult", dom.getFirstChild());
				        	  Node getWeatherResultNode = XPath.selectNode("//wea:GetWeatherResponse", dom, nameSpaceList);
				        	  System.out.println(getWeatherResultNode.getNodeName());
		        			  System.out.println("##########"+getWeatherResultNode.getTextContent()+"\n######");
				        	  
				        	  
				        	  return ok("toto"+response.getStatus()+"--"+response.getHeader(CONTENT_TYPE)+"--\n"+getWeatherResultNode.getTextContent());
				          }
				        }
				      )
				     );


    	
//        return ok(index.render(responseStr));
    }
    
    /*
     * Test purpose 
     * 
     */
    /*
    public static Result getWeather(String feedUrl) {
        return async(
          WS.url(feedUrl).get().map(
            new Function<WS.Response, Result>() {
              public Result apply(WS.Response response) {
                return ok("Feed title:" + response.asJson().findPath("title"));
              }
            }
          )
        );
      }
      */
  
    
}
