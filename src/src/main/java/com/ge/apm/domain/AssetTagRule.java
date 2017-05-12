/*
 */
package com.ge.apm.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;


/**
 *
 * @author 212601104
 */
@Entity
@Table(name = "asset_tag_rule")
public class AssetTagRule  implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "asset_id")
    private Integer assetId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "tag_id")
    private Integer tagId;

    @JoinColumn(name = "tag_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private AssetTag assetTag;

    @JoinColumn(name = "asset_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private AssetInfo assetInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public Integer getTagId() {
        return tagId;
    }

    public AssetTag getAssetTag() {
        return assetTag;
    }

    public void setAssetTag(AssetTag assetTag) {
        this.assetTag = assetTag;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }
}
