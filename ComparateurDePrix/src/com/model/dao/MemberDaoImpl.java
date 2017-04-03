package com.model.dao;

import static com.model.dao.DAOUtilitaire.fermeturesSilencieuses;
import static com.model.dao.DAOUtilitaire.initializingPreparedQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.model.beans.Member;
//import java.sql.Statement;

public class MemberDaoImpl implements MemberDao {
    private DAOFactory          daoFactory;
    private static final String SQL_SELECT_PAR_EMAIL = "SELECT memberID,pseudonym,email,password,"
            + "registrationDate FROM member WHERE email = ?";
    private static final String SQL_INSERT           = "INSERT INTO member (pseudonym, email, password, registrationDate)"
            + " VALUES (?, ?, ?, NOW())";

    MemberDaoImpl( DAOFactory daoFactory ) {
        this.daoFactory = daoFactory;
    }

    /*
     * Simple méthode utilitaire permettant de faire la correspondance (le
     * mapping) entre une ligne issue de la table des utilisateurs (un
     * ResultSet) et un bean Utilisateur.
     */
    private static Member map( ResultSet resultSet ) throws SQLException {
        Member member = new Member();
        member.setMemberId( resultSet.getLong( "memberID" ) );
        member.setEmail( resultSet.getString( "email" ) );
        member.setPassword( resultSet.getString( "password" ) );
        member.setPseudonym( resultSet.getString( "pseudonym" ) );
        member.setRegistrationDate( resultSet.getTimestamp( "registrationDate" ) );
        return member;
    }

    @Override
    public Member find( String email ) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Member member = null;
        try {
            /* Récupération d'une connexion depuis la Factory */
            connection = daoFactory.getConnection();
            preparedStatement = initializingPreparedQuery( connection, SQL_SELECT_PAR_EMAIL, false, email );
            resultSet = preparedStatement.executeQuery();
            /*
             * Parcours de la ligne de données de l'éventuel ResulSet retourné
             */
            if ( resultSet.next() ) {
                member = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connection );
        }

        return member;
    }

    @Override
    public void create( Member member ) throws IllegalArgumentException, DAOException {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;
        try {
            /* Récupération d'une connexion depuis la Factory */
            connection = daoFactory.getConnection();
            preparedStatement = initializingPreparedQuery( connection, SQL_INSERT, true, member.getPseudonym(),
                    member.getEmail(), member.getPassword() );
            int statut = preparedStatement.executeUpdate();
            /* Analyse du statut retourné par la requête d'insertion */
            if ( statut == 0 ) {
                throw new DAOException( "Échec de la création de l'utilisateur, aucune ligne ajoutée dans la table." );
            }
            /* Récupération de l'id auto-généré par la requête d'insertion */
            valeursAutoGenerees = preparedStatement.getGeneratedKeys();
            if ( valeursAutoGenerees.next() ) {
                /*
                 * Puis initialisation de la propriété id du bean Utilisateur
                 * avec sa valeur
                 */
                member.setMemberId( valeursAutoGenerees.getLong( 1 ) );
            } else {
                throw new DAOException(
                        "Échec de la création de l'utilisateur en base, aucun ID auto-généré retourné." );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( valeursAutoGenerees, preparedStatement, connection );
        }
    }
}