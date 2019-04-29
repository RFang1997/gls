package simplepdl.manip;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import petrinet.PetriNet;
import petrinet.PetrinetFactory;
import petrinet.PetrinetPackage;
import petrinet.Place;
import petrinet.Node;
import petrinet.Arc;
import petrinet.ArcKind;
import petrinet.Transition;

import simplepdl.Process;
import simplepdl.Ressource;
import simplepdl.WorkDefinition;
import simplepdl.WorkSequence;
import simplepdl.WorkSequenceType;
import simplepdl.SimplepdlFactory;
import simplepdl.SimplepdlPackage;

import simplepdl.WorkSequenceType;

public class SimplePDL2PetriNet {
	
	
	public static Node rechercheNoeud(ArrayList<Node> liste,String nomNoeud) {
		for(int i=0; i<liste.size();i++) {
			if(liste.get(i).getName().equals(nomNoeud)) {
				return liste.get(i);
			}
		}
		return null;
		
	}

	public static void main(String[] args) {
		
		// Chargement du package petrinet afin de l'enregistrer dans le registre d'Eclipse.
		PetrinetPackage packageInstance1 = PetrinetPackage.eINSTANCE;
		SimplepdlPackage packageInstance2 = SimplepdlPackage.eINSTANCE;
		
		// Enregistrer l'extension ".xmi" comme devant Ãªtre ouverte Ã 
		// l'aide d'un objet "XMIResourceFactoryImpl"
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());
		
		// Créer un objet resourceSetImpl qui contiendra le modèle simplePDL
		ResourceSet resSet = new ResourceSetImpl();
		
		// Charger la ressource (le modèle simplepdl)
		URI modelURI = URI.createURI("models/SimplePDLCreator_Created_Process.xmi");
		Resource resource = resSet.getResource(modelURI, true);
		
		// Créer un objet resourceSetImpl qui contiendra le modèle PetriNet associé au modèle précédent
		ResourceSet resSetbis = new ResourceSetImpl();
		
		// Définir la ressource (le modèle petrinet)
		URI modelURIbis = URI.createURI("models/SimplePDL2PetriNet.xmi");
		Resource resourcebis = resSetbis.createResource(modelURIbis);
			
		// Récupérer le premier élément du modèle simplepdl (élément racine)
		Process process = (Process) resource.getContents().get(0);
		
		// La fabrique pour fabriquer les éléments de SimplePDL
	    PetrinetFactory myFactory = PetrinetFactory.eINSTANCE;
	    
	    // Créer un élément Petrinet
	 	PetriNet petrinet = myFactory.createPetriNet();
	 	petrinet.setName("Mon premier petrinet");
	 		
	 	// Ajouter le Petrinet dans le modèle
		resourcebis.getContents().add(petrinet);
		
		//Création de listes de stockage
		ArrayList<Node> noeuds = new ArrayList<Node>();
		
		/**
		 * Manipulation de notre instance
		 */	
		//Transformation SimplePDL2PetriNet
		for (Object o : process.getProcesselement()) {
			//Transformation WorkDefinition
			if (o instanceof WorkDefinition) {
				//Création des éléments petrinet
				Place p_ready=myFactory.createPlace();
				Place p_started=myFactory.createPlace();
				Place p_finished=myFactory.createPlace();   
				Transition t_ready_started = myFactory.createTransition();
				Transition t_started_finished = myFactory.createTransition(); 
				Arc a_ready_trs = myFactory.createArc();
				Arc a_trs_started = myFactory.createArc();
				Arc a_started_tsf = myFactory.createArc();
				Arc a_tsf_finished = myFactory.createArc();
				
				//Paramètrage des attributs
				p_ready.setName(((WorkDefinition) o).getName() + "_ready");
				p_started.setName(((WorkDefinition) o).getName() + "_started");
				p_finished.setName(((WorkDefinition) o).getName() + "_finished");
				p_ready.setMarking(1);
				p_started.setMarking(0);
				p_finished.setMarking(0);
				
				petrinet.getNode().add(p_ready);
				petrinet.getNode().add(p_started);
				petrinet.getNode().add(p_finished);
				
				t_ready_started.setName(((WorkDefinition) o).getName() + "_ready_started");
				t_started_finished.setName(((WorkDefinition) o).getName() + "_started_finished");
				t_ready_started.setMax_time(0);
				t_started_finished.setMax_time(0);
				t_ready_started.setMin_time(0);
				t_started_finished.setMin_time(0);
				
				petrinet.getNode().add(t_ready_started);
				petrinet.getNode().add(t_started_finished);
				
				noeuds.add(p_ready);
				noeuds.add(p_started);
				noeuds.add(p_finished);
				noeuds.add(t_ready_started);
				noeuds.add(t_started_finished);
							
				a_ready_trs.setKind(ArcKind.NORMAL);
				a_trs_started.setKind(ArcKind.NORMAL);
				a_started_tsf.setKind(ArcKind.NORMAL);
				a_tsf_finished.setKind(ArcKind.NORMAL);	
				a_ready_trs.setWeight(1);
				a_trs_started.setWeight(1);
				a_started_tsf.setWeight(1);
				a_tsf_finished.setWeight(1);
				a_ready_trs.setSource(p_ready);
				a_trs_started.setSource(t_ready_started);
				a_started_tsf.setSource(p_started);
				a_tsf_finished.setSource(t_started_finished);
				a_ready_trs.setTarget(t_ready_started);
				a_trs_started.setTarget(p_started);	
				a_started_tsf.setTarget(t_started_finished);
				a_tsf_finished.setTarget(p_finished);
																						
				petrinet.getArc().add(a_ready_trs);
				petrinet.getArc().add(a_trs_started);
				petrinet.getArc().add(a_started_tsf);
				petrinet.getArc().add(a_tsf_finished);
							
			} else if (o instanceof WorkSequence) {
				Arc a_ws = myFactory.createArc();
				
				a_ws.setKind(ArcKind.READ_ARC);
				a_ws.setWeight(1);
				
				if (((WorkSequence) o).getLinkType() == WorkSequenceType.FINISH_TO_START || ((WorkSequence) o).getLinkType() == WorkSequenceType.FINISH_TO_FINISH) {			 
					Node noeud=rechercheNoeud(noeuds,((WorkSequence) o).getPredecessor().getName() + "_finished");
					a_ws.setSource(noeud);
					
				} else {		
					Node noeud=rechercheNoeud(noeuds,((WorkSequence) o).getPredecessor().getName() + "_started");
					a_ws.setSource(noeud);	
				}
				
				if (((WorkSequence) o).getLinkType() == WorkSequenceType.FINISH_TO_START || ((WorkSequence) o).getLinkType() == WorkSequenceType.START_TO_START) {
					Node noeud=rechercheNoeud(noeuds,((WorkSequence) o).getSuccessor().getName() + "_ready_started");
					a_ws.setTarget(noeud);
					
				} else {
					Node noeud=rechercheNoeud(noeuds,((WorkSequence) o).getSuccessor().getName() + "_started_finished");
					a_ws.setTarget(noeud);
				}
				petrinet.getArc().add(a_ws);
				
			} else if (o instanceof Ressource) {
				Place p_ressource=myFactory.createPlace();
				p_ressource.setName(((Ressource) o).getName());
				p_ressource.setMarking(((Ressource) o).getCount());
				petrinet.getNode().add(p_ressource);
			}
		}
		
		// Sauver la ressource
	    try {
	    	resourcebis.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}

	
		
		
		
		