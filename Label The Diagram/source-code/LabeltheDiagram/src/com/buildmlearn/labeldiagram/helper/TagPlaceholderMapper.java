package com.buildmlearn.labeldiagram.helper;

import com.example.labelthediagram.R;
import android.util.SparseIntArray;

/**
 * This class is to map dragging textView tags to relevant 
 * imageView place holders so that to identify that the user
 * has correctly labeled the diagram in DiagramPlay activity
 */
public class TagPlaceholderMapper {
	
	private SparseIntArray tagMapper;
		
	public TagPlaceholderMapper(){
		
		tagMapper = new SparseIntArray();
		
	}
	
	// Call the correct method according to the diagram name
	public SparseIntArray diagramMapper(String diagramName){
		
		if(!diagramName.equals(null)){
			switch(diagramName){
			
			case "HumanEye":
				getHumanEyeTagMap();
				break;
			case "HumanEar":
				getHumanEarTagMap();
				break;
			case "HumanHeart":
				getHumanHeartTagMap();
				break;
			case "PlantCell":
				getPlantCellTagMap();
				break;
			case "PlantFlower":
				getPlantFlowerTagMap();
				break;
			case "Bacteria":
				getBacteriaTagMap();
				break;
			
			}
		}
		
		return tagMapper;
	}
	
	/*
	 * Map correct draggable Tags(TextViews) to droppable place holders(ImageViews
	 * in Bacteria diagram
	 */
	private SparseIntArray getBacteriaTagMap() {

		tagMapper.put(R.id.chromosomeBlb, R.id.chromosomeTag);
		tagMapper.put(R.id.ribosomesBlb, R.id.ribosomesTag);
		tagMapper.put(R.id.food_granuleBlb, R.id.food_granuleTag);
		tagMapper.put(R.id.piliBlb, R.id.piliTag);
		tagMapper.put(R.id.flagellumBlb, R.id.flagellumTag);
		tagMapper.put(R.id.plasmidBlb, R.id.plasmidTag);
		tagMapper.put(R.id.capsuleBlb, R.id.capsuleTag);
		tagMapper.put(R.id.cellwallBlb, R.id.cellwallTag);
		tagMapper.put(R.id.cytoplasmBlb, R.id.cytoplasmTag);
		tagMapper.put(R.id.plasma_membraneBlb, R.id.plasma_membraneTag);
		
		return tagMapper;

		
	}

	/*
	 * Map correct draggable Tags(TextViews) to droppable place holders(ImageViews
	 * in PlantFlower diagram
	 */
	private SparseIntArray getPlantFlowerTagMap() {

		tagMapper.put(R.id.antherBlb, R.id.antherTag);
		tagMapper.put(R.id.petalBlb, R.id.petalTag);
		tagMapper.put(R.id.receptacleBlb, R.id.receptacleTag);
		tagMapper.put(R.id.pedicelBlb, R.id.pedicelTag);
		tagMapper.put(R.id.stigmaBlb, R.id.stigmaTag);
		tagMapper.put(R.id.styleBlb, R.id.styleTag);
		tagMapper.put(R.id.overyBlb, R.id.overyTag);
		tagMapper.put(R.id.filamentBlb, R.id.filamentTag);
		tagMapper.put(R.id.sepalBlb, R.id.sepalTag);
		tagMapper.put(R.id.ovuleBlb, R.id.ovuleTag);
		
		return tagMapper;
		
	}

	/*
	 * Map correct draggable Tags(TextViews) to droppable place holders(ImageViews
	 * in PlantCell diagram
	 */
	private SparseIntArray getPlantCellTagMap() {

		tagMapper.put(R.id.ribosomesBlb, R.id.ribosomeTag);
		tagMapper.put(R.id.smootherBlb, R.id.smootherTag);
		tagMapper.put(R.id.rougherBlb, R.id.rougherTag);
		tagMapper.put(R.id.nucleusBlb, R.id.nucleusTag);
		tagMapper.put(R.id.vacuoleBlb, R.id.vacuoleTag);
		tagMapper.put(R.id.cellwallBlb, R.id.cellwallTag);
		tagMapper.put(R.id.cell_membraneBlb, R.id.cell_membraneTag);
		tagMapper.put(R.id.mitochondriumBlb, R.id.mitochondiumTag);
		tagMapper.put(R.id.chloroplastBlb, R.id.chloroplastTag);
		tagMapper.put(R.id.golgi_complexBlb, R.id.golgi_complexTag);
		
		return tagMapper;
		
	}

	/*
	 * Map correct draggable Tags(TextViews) to droppable place holders(ImageViews
	 * in HumanEye diagram
	 */
	private SparseIntArray getHumanEyeTagMap() {
		
		tagMapper.put(R.id.ciliaryBodyBlb, R.id.ciliaryTag);
		tagMapper.put(R.id.irisBlb, R.id.irisTag);
		tagMapper.put(R.id.pupilBlb, R.id.pupilTag);
		tagMapper.put(R.id.lensBlb, R.id.lensTag);
		tagMapper.put(R.id.corneaBlb, R.id.corneaTag);
		tagMapper.put(R.id.vitreousBlb, R.id.vitreousTag);
		tagMapper.put(R.id.opticNerveBlb, R.id.nerveTag);
		tagMapper.put(R.id.blindSpotBlb, R.id.opticdiskTag);
		tagMapper.put(R.id.foveaBlb, R.id.foveaTag);
		tagMapper.put(R.id.retinaBlb, R.id.retinaTag);
		
		return tagMapper;
		
	}

	/*
	 * Map correct draggable Tags(TextViews) to droppable place holders(ImageViews
	 * in HumanHeart diagram
	 */
	private SparseIntArray getHumanHeartTagMap() {
		
		tagMapper.put(R.id.sup_vena_cavaBlb, R.id.sup_vena_cavaTag);
		tagMapper.put(R.id.right_atriumBlb, R.id.right_atriunTag);
		tagMapper.put(R.id.inf_vena_cavaBlb, R.id.inf_vena_cavaTag);
		tagMapper.put(R.id.right_ventricleBlb, R.id.right_ventricleTag);
		tagMapper.put(R.id.left_ventricleBlb, R.id.left_ventricleTag);
		tagMapper.put(R.id.left_atriumBlb, R.id.left_atriumTag);
		tagMapper.put(R.id.pul_arteryBulb, R.id.pul_arteryTag);
		tagMapper.put(R.id.aortaBlb, R.id.aortaTag);
		tagMapper.put(R.id.pul_veinBlb, R.id.pul_veinTag);
		
		return tagMapper;
	}

	/*
	 * Map correct draggable Tags(TextViews) to droppable place holders(ImageViews
	 * in HumanEar diagram
	 */
	private SparseIntArray getHumanEarTagMap() {

		tagMapper.put(R.id.pinnaBlb, R.id.pinnaTag);
		tagMapper.put(R.id.earcanalBlb, R.id.ear_canalTag);
		tagMapper.put(R.id.eardrumBlb, R.id.eardrumTag);
		tagMapper.put(R.id.cochleaBlb, R.id.cochleaTag);
		tagMapper.put(R.id.earnerveBlb, R.id.earnerveTag);
		tagMapper.put(R.id.malleusBlb, R.id.malleusTag);
		tagMapper.put(R.id.incusBlb, R.id.incusTag);
		tagMapper.put(R.id.outerearBlb, R.id.outerearTag);
		tagMapper.put(R.id.stapesBlb, R.id.stapesTag);
		tagMapper.put(R.id.canalsBlb, R.id.canalsTag);
		
		return tagMapper;
		
	}

	

}
