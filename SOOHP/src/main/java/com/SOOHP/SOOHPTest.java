package com.SOOHP;



import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

///import java.math.BigDecimal;


import org.kie.api.runtime.rule.FactHandle;

import com.SOOHP.Clue;


public class SOOHPTest {

	

	
    public static final void main(String[] args) {
        try {
            // load up the knowledge base
	        KieServices ks = KieServices.Factory.get();
    	    KieContainer kContainer = ks.getKieClasspathContainer();
        	KieSession kSession = kContainer.newKieSession("ksession-rules");

            // go !
            Clue newClue = new Clue(1);
            FactHandle clueHandle = kSession.insert(newClue);

            
            kSession.fireAllRules();
            newClue.setClueName(2);            
            kSession.update(clueHandle, newClue);
            
            
            kSession.fireAllRules();
        	
  	
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }



}

