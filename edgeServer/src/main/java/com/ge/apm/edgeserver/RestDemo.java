/*
 */
package com.ge.apm.edgeserver;

import org.apache.camel.builder.RouteBuilder;

/**
 *
 * @author 212547631
 */
public class RestDemo extends RouteBuilder {

    @Override
    public void configure() {

        rest("/persons")
            .post().to("direct:postPersons")
            .get().to("direct:getPersons")
            .get("/{personId}").to("direct:getPersonId")
            .put("/{personId}").to("direct:putPersonId")
            .delete("/{personId}").to("direct:deletePersonId");
        
        from("direct:postPersons")
            .setBody(simple("insert into person(firstName, lastName) values('${header.firstName}','${header.lastName}')"))
            .to("jdbc:dataSource")
            .setBody(simple("select * from person where id in (select max(id) from person)"))
            .to("jdbc:dataSource");

        from("direct:getPersons")
            .setBody(simple("select * from person"))
            .to("jdbc:dataSource");
        
        from("direct:getPersonId")
            .setBody(simple("select * from person where id = ${header.personId}"))  
            .to("jdbc:dataSource");
        
        from("direct:putPersonId")            
            .setBody(simple("update person set firstName='${header.firstName}', lastName='${header.lastName}' where id = ${header.personId}"))              
            .to("jdbc:dataSource");

        from("direct:deletePersonId")
            .setBody(simple("delete from person where id = ${header.personId}"))
            .to("jdbc:dataSource");
        
    }
}
