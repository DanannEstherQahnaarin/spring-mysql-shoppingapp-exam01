package com.example.shopping.domain.entity.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrderItem is a Querydsl query type for OrderItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderItem extends EntityPathBase<OrderItem> {

    private static final long serialVersionUID = 5320747L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrderItem orderItem = new QOrderItem("orderItem");

    public final QOrders order;

    public final NumberPath<Long> orderItemId = createNumber("orderItemId", Long.class);

    public final NumberPath<Integer> priceAtOrder = createNumber("priceAtOrder", Integer.class);

    public final com.example.shopping.domain.entity.product.QProduct product;

    public final NumberPath<Integer> qty = createNumber("qty", Integer.class);

    public QOrderItem(String variable) {
        this(OrderItem.class, forVariable(variable), INITS);
    }

    public QOrderItem(Path<? extends OrderItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrderItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrderItem(PathMetadata metadata, PathInits inits) {
        this(OrderItem.class, metadata, inits);
    }

    public QOrderItem(Class<? extends OrderItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.order = inits.isInitialized("order") ? new QOrders(forProperty("order")) : null;
        this.product = inits.isInitialized("product") ? new com.example.shopping.domain.entity.product.QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

