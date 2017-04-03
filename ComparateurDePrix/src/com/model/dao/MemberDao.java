//les méthodes d'une interface 
//sont obligatoirement publiques et abstraites
package com.model.dao;

import com.model.beans.Member;

public interface MemberDao {
    //la création d'un utilisateur, lors de son inscription ;
	void create( Member member ) throws DAOException ;
	//la recherche d'un utilisateur, lors de la connexion
	Member find( String email ) throws DAOException ;
	
	
}
