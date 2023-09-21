import {useKeycloak} from "@react-keycloak/web";
import {Shell} from "../shell/Shell";
import React from "react";
import {ErrorPage} from "../../components/error/ErrorPage";
import {UserProvider} from "../../hooks/user-provider/UserProvider";

const AuthenticatedTemplate = () => {
    const {keycloak} = useKeycloak();
    return (
            keycloak.authenticated ?
                (
                    <UserProvider>
                        <Shell/>
                    </UserProvider>
                    )
                :
                (<ErrorPage text="You are not logged in."/>)
        )


}

export default AuthenticatedTemplate;