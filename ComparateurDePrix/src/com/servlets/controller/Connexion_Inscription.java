package com.servlets.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.forms.ConnexionForm;
import com.forms.InscriptionForm;
import com.model.beans.Member;
import com.model.dao.DAOFactory;
import com.model.dao.MemberDao;

public class Connexion_Inscription extends HttpServlet {
	public static final String CONF_DAO_FACTORY = "daofactory";
	public static final String ATT_USER1 = "memberInscri";
	public static final String ATT_FORM1 = "formInscri";
	public static final String ATT_SESSION_MEMBER = "sessionMember";
	public static final String ATT_USER2 = "memberConncet";
	public static final String ATT_FORM2 = "formConnect";

	public static final String VUE_INSC = "/WEB-INF/inscription.jsp";
	/* champs du formulaire connexion et inscription */
	public static final String BUTTON_SUBMIT_FORM = "bouton_validation";
	private MemberDao memberDao;

	/* la methode init() s'execute une seule fois */
	public void init() throws ServletException {
		/* Récupération d'une instance de notre DAO Utilisateur */
		this.memberDao = ((DAOFactory) getServletContext().getAttribute(CONF_DAO_FACTORY)).getMemberDao();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* Affichage de la page d'inscription */
		this.getServletContext().getRequestDispatcher(VUE_INSC).forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* Préparation de l'objet formulaire */

		HttpSession session = request.getSession();
		String value = request.getParameter("bouton_validation");
		if (value.equals("inscription")) {
			InscriptionForm form = new InscriptionForm(memberDao);

			/* Traitement de la requête et récupération du bean en résultant */
			Member member = form.inscrireMember(request);

			/**
			 * Si aucune erreur de validation n'a eu lieu, alors ajout du bean
			 * Utilisateur à la session, sinon suppression du bean de la
			 * session.
			 */
			if (form.getErreurs().isEmpty()) {
				session.setAttribute(ATT_SESSION_MEMBER, member);
			} else {
				session.setAttribute(ATT_SESSION_MEMBER, null);
			}

			/* Stockage du formulaire et du bean dans l'objet request */
			request.setAttribute(ATT_FORM1, form);
			request.setAttribute(ATT_USER1, member);

		} else if (value.equals("connexion")) {
			ConnexionForm form = new ConnexionForm(memberDao);
			Member member = form.connecterMember(request);
			/**
			 * Si aucune erreur de validation n'a eu lieu, alors ajout du bean
			 * Utilisateur à la session, sinon suppression du bean de la
			 * session.
			 */
			if (form.getErreurs().isEmpty()) {
				session.setAttribute(ATT_SESSION_MEMBER, member);
			} else {
				session.setAttribute(ATT_SESSION_MEMBER, null);
			}
			request.setAttribute(ATT_FORM2, form);
			request.setAttribute(ATT_USER2, member);
		}

		this.getServletContext().getRequestDispatcher(VUE_INSC).forward(request, response);
	}
}