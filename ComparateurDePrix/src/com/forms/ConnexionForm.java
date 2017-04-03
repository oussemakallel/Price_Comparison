package com.forms;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jasypt.util.password.ConfigurablePasswordEncryptor;

import com.model.beans.Member;
import com.model.dao.DAOException;
import com.model.dao.MemberDao;

public class ConnexionForm {

	private static final String CHAMP_EMAIL = "email";
	private static final String CHAMP_PASS = "motdepasse";
	private static final String AUTHEN = "authentification"  ;
	
	// private static final String CHAMP_CONF = "confirmation";
	// private static final String CHAMP_NOM = "nom";

	private static final String ALGO_CHIFFREMENT = "SHA-256";

	private String resultat;
	private Map<String, String> erreurs = new HashMap<String, String>();
	private MemberDao memberDao;
    private String  htmlClassSucess ;
    private String  htmlClassFail ;
	
	public ConnexionForm(MemberDao memberDao) {
		this.memberDao = memberDao;
	}

	public Map<String, String> getErreurs() {
		return erreurs;
	}

	public String getResultat() {
		return resultat;
	}
	
	public String getHtmlClassSucess()
	{
		return htmlClassSucess ;
	}
	public String getHtmlClassFail()
	{
		return htmlClassFail ;
	}

	public Member connecterMember(HttpServletRequest request) {
		String email = getValeurChamp(request, CHAMP_EMAIL);
		String motDePasse = getValeurChamp(request, CHAMP_PASS);
		// String confirmation = getValeurChamp(request, CHAMP_CONF);
		// String nom = getValeurChamp(request, CHAMP_NOM);

		Member member = new Member(); //avant connexion 
		Member memberEnConnexion = new Member(); 
		try {
			traiterEmail(email, member);
			traiterMotsDePasse(motDePasse, member);
			memberEnConnexion=traiterMotDePasseEmail(member) ; // null ou ok 
			// traiterNom(nom, member);

			if (erreurs.isEmpty()) {
				//memberDao.create(member);
				resultat = "Succès de la connexion.";
				htmlClassSucess="alert alert-success" ;
			} else {
				resultat = "Échec d'authentification.";
				htmlClassFail="alert alert-danger alert-dismissible" ;
			}
		} catch (DAOException e) {
			resultat = "Échec de la connexion : une erreur imprévue est survenue, merci de réessayer dans quelques instants.";
			e.printStackTrace();
		}

		return memberEnConnexion;
	}

	/*
	 * Appel à la validation de l'adresse email reçue et initialisation de la
	 * propriété email du bean
	 */
	private void traiterEmail(String email, Member member) {
		try {
			validationEmail(email);
		} catch (FormValidationException e) {
			setErreur(CHAMP_EMAIL, e.getMessage());
		}
		member.setEmail(email);
	}

	/*
	 * Appel à la validation des mots de passe reçus, chiffrement du mot de
	 * passe et initialisation de la propriété motDePasse du bean
	 */
	private void traiterMotsDePasse(String motDePasse, Member member) {
		try {
			validationMotsDePasse(motDePasse);
		} catch (FormValidationException e) {
			setErreur(CHAMP_PASS, e.getMessage());
			// setErreur(CHAMP_CONF, null);
		}

		/*
		 * Utilisation de la bibliothèque Jasypt pour chiffrer le mot de passe
		 * efficacement.
		 * 
		 * L'algorithme SHA-256 est ici utilisé, avec par défaut un salage
		 * aléatoire et un grand nombre d'itérations de la fonction de hashage.
		 * 
		 * La String retournée est de longueur 56 et contient le hash en Base64.
		 */
		
		member.setPassword(motDePasse);
	}

	/*
	 * Appel à la validation du nom reçu et initialisation de la propriété nom
	 * du bean
	 */
	/*
	 * private void traiterNom(String nom, Member member) { try {
	 * validationNom(nom); } catch (FormValidationException e) {
	 * setErreur(CHAMP_NOM, e.getMessage()); } member.setPseudonym(nom); }
	 */

	/* Validation de l'adresse email */
	private void validationEmail(String email) throws FormValidationException {
		if (email != null) {
			if (!email.matches("([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)")) {
				throw new FormValidationException("Merci de saisir une adresse mail valide.");
			}
		} else {
			throw new FormValidationException("Merci de saisir une adresse mail.");
		}
	}

	/* Validation des mots de passe */
	private void validationMotsDePasse(String motDePasse) throws FormValidationException {
		if (motDePasse != null) {
			if (motDePasse.length() < 3) {
				throw new FormValidationException("Les mots de passe doivent contenir au moins 3 caractères.");
			}
		} else {
			throw new FormValidationException("Merci de saisir votre mot de passe.");
		}
	}

	private Member validationMotDePasseEmail(Member member)throws FormValidationException {
		ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
		passwordEncryptor.setAlgorithm(ALGO_CHIFFREMENT);
		passwordEncryptor.setPlainDigest(false);
		 
		Member memberTemp = new Member();
		if ( ((memberTemp = memberDao.find(member.getEmail()))== null) || ( ! passwordEncryptor.checkPassword(member.getPassword(), memberTemp.getPassword()) ) 
				){

			throw new FormValidationException("Échec d'authentification");
		
		}
		
       return memberTemp ; //null ou ok 
	}
	private Member traiterMotDePasseEmail(Member member) {
		Member memberTemp = null ;
		try {
			memberTemp=validationMotDePasseEmail(member );
		} catch (FormValidationException e) {
			setErreur(AUTHEN, e.getMessage());
		}
		return memberTemp ; //null ou ok 
		//member.setEmail(email);
	}

	/* Validation du nom */
	/*
	 * private void validationNom(String nom) throws FormValidationException {
	 * if (nom != null && nom.length() < 3) { throw new
	 * FormValidationException("Le nom d'utilisateur doit contenir au moins 3 caractères."
	 * ); } }
	 */

	/*
	 * Ajoute un message correspondant au champ spécifié à la map des erreurs.
	 */
	private void setErreur(String champ, String message) {
		erreurs.put(champ, message);
	}

	/*
	 * Méthode utilitaire qui retourne null si un champ est vide, et son contenu
	 * sinon.
	 */
	private static String getValeurChamp(HttpServletRequest request, String nomChamp) {
		String valeur = request.getParameter(nomChamp);
		if (valeur == null || valeur.trim().length() == 0) {
			return null;
		} else {
			return valeur;
		}
	}
}
