package server;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.io.*;

import org.ejml.simple.SimpleMatrix;

import edu.stanford.nlp.sentiment.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

@Controller
public class SentimentController {

	StanfordCoreNLP tokenizer;
	StanfordCoreNLP pipeline;

	public SentimentController() {
		// We construct two pipelines.  One handles tokenization, if
		// necessary.  The other takes tokenized sentences and converts
		// them to sentiment trees.
		Properties pipelineProps = new Properties();
		Properties tokenizerProps = new Properties();

		pipelineProps.setProperty("ssplit.isOneSentence", "true");
		pipelineProps.setProperty("annotators", "parse, sentiment");
		pipelineProps.setProperty("enforceRequirements", "false");

		tokenizerProps.setProperty("annotators", "tokenize, ssplit");

		this.tokenizer = new StanfordCoreNLP(tokenizerProps);
		this.pipeline = new StanfordCoreNLP(pipelineProps);

	}

	@RequestMapping("/sentiment")
	public @ResponseBody HashMap<Integer,HashMap<String,Object>> sentiment(@RequestParam(value="lines", required=true) List<String> lines, Model model) {

		HashMap<Integer,HashMap<String,Object>> response = new HashMap<Integer,HashMap<String,Object>>();

		for(int i = 0; i < lines.size(); i++) {

			response.put(i,new HashMap<String,Object>());

			Annotation annotation = tokenizer.process(lines.get(i));
			pipeline.annotate(annotation);
			if(annotation.get(CoreAnnotations.SentencesAnnotation.class).size() > 0) {
				CoreMap sentence = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
				Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
				SimpleMatrix vector = RNNCoreAnnotations.getPredictions(tree);

				response.get(i).put("line",lines.get(i));
				System.out.println("Parsing "+i+": "+lines.get(i));
				response.get(i).put("sentiment",vector.get(1)*0.25+vector.get(2)*0.5+vector.get(3)*0.75+vector.get(4));
			}
		}
		return response;
	}
}
